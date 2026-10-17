// Copyright 2026 ESC5P4/Kauri
// Licensed under the Apache License, Version 2.0. See LICENSE.

package com.henshin.client.relay

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelFuture
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioDatagramChannel
import org.cloudburstmc.netty.channel.raknet.RakChannelFactory
import org.cloudburstmc.netty.channel.raknet.config.RakChannelOption
import org.cloudburstmc.netty.handler.codec.raknet.server.RakServerRateLimiter
import org.cloudburstmc.protocol.bedrock.BedrockPeer
import org.cloudburstmc.protocol.bedrock.BedrockPong
import org.cloudburstmc.protocol.bedrock.BedrockServerSession
import org.cloudburstmc.protocol.bedrock.PacketDirection
import org.cloudburstmc.protocol.bedrock.codec.v844.Bedrock_v844
import org.cloudburstmc.protocol.bedrock.netty.initializer.BedrockChannelInitializer
import java.net.InetSocketAddress
import kotlin.random.Random

/**
 * Paso A del relay: escucha en UDP [port] y registra cada conexión entrante.
 * Todavía no reenvía nada al servidor real.
 */
class RelayListener(
    private val port: Int = 19132,
    private val log: (String) -> Unit
) {
    private var group: NioEventLoopGroup? = null
    private var future: ChannelFuture? = null

    val isRunning: Boolean get() = future != null

    fun start() {
        if (isRunning) return

        val eventLoops = NioEventLoopGroup()
        group = eventLoops

        val codec = Bedrock_v844.CODEC
        val pong = BedrockPong()
            .edition("MCPE")
            .gameType("Survival")
            .version(codec.minecraftVersion)
            .protocolVersion(codec.protocolVersion)
            .motd("Henshin Client")
            .subMotd("Relay de prueba")
            .playerCount(0)
            .maximumPlayerCount(1)
            .nintendoLimited(false)
            .ipv4Port(port)
            .ipv6Port(port)

        future = ServerBootstrap()
            .group(eventLoops)
            .channelFactory(RakChannelFactory.server(NioDatagramChannel::class.java))
            .option(RakChannelOption.RAK_ADVERTISEMENT, pong.toByteBuf())
            .option(RakChannelOption.RAK_GUID, Random.nextLong())
            .childHandler(object : BedrockChannelInitializer<BedrockServerSession>() {

                override fun createSession0(peer: BedrockPeer, subClientId: Int): BedrockServerSession {
                    log("Conexión entrante (subClient $subClientId)")
                    return BedrockServerSession(peer, subClientId).also {
                        it.codec = codec
                    }
                }

                override fun initSession(session: BedrockServerSession) {}

                override fun preInitChannel(channel: Channel) {
                    channel.attr(PacketDirection.ATTRIBUTE).set(PacketDirection.CLIENT_BOUND)
                    super.preInitChannel(channel)
                }
            })
            .localAddress(InetSocketAddress("0.0.0.0", port))
            .bind()
            .awaitUninterruptibly()
            .also { it.channel().pipeline().remove(RakServerRateLimiter.NAME) }

        log("Escuchando en UDP $port (Bedrock ${codec.minecraftVersion})")
    }

    fun stop() {
        future?.channel()?.close()?.awaitUninterruptibly()
        group?.shutdownGracefully()?.awaitUninterruptibly()
        future = null
        group = null
        log("Relay detenido")
    }
}
