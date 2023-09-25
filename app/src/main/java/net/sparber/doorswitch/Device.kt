package net.sparber.doorswitch


class Device (address: String, port: Int, key: String) {
    // The device id doesn't matter
    val id: String = ""
    val key: String = key
    val address: String = "http://$address:$port"
}