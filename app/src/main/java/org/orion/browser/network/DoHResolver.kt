package org.orion.browser.network

import android.content.Context
import okhttp3.Dns
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.dnsoverhttps.DnsOverHttps
import java.net.InetAddress
import java.util.concurrent.TimeUnit

object DoHResolver {

    private lateinit var okHttpClient: OkHttpClient
    private lateinit var dnsOverHttps: DnsOverHttps

    fun initialize(context: Context) {
        val bootstrapClient = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .build()

        dnsOverHttps = DnsOverHttps.Builder()
            .client(bootstrapClient)
            .url("https://1.1.1.1/dns-query".toHttpUrl())
            .bootstrapDnsHosts(
                InetAddress.getByName("1.1.1.1"),
                InetAddress.getByName("1.0.0.1"),
                InetAddress.getByName("2606:4700:4700::1111"),
                InetAddress.getByName("2606:4700:4700::1001")
            )
            .includeIPv6(true)
            .build()

        okHttpClient = bootstrapClient.newBuilder()
            .dns(dnsOverHttps)
            .build()
    }

    fun getClient(): OkHttpClient = okHttpClient

    fun getDns(): Dns = dnsOverHttps
}
