package com.mamh.smartwardrobe.data.serialize

import org.json.JSONObject


/**
@description Class to build command datagrams for smart wardrobe control
@author Mamh
@Date 2024/4/21
 **/


class CommandDatagram private constructor(
    private val deviceId: Int,
    private val from: Int,
    private val packetType: String,
    private val command: String,
    private val action: String,
    private val remark: String
) {
    class Builder {
        private var deviceId: Int = 1
        private var from: Int = 0
        private var packetType: String = "Command"
        private var command: String = "Default"
        private var action: String = "default"
        private var remark: String = "default remark"

        fun setDeviceId(deviceId: Int): Builder {
            this.deviceId = deviceId
            return this
        }

        fun setFrom(from: Int): Builder {
            this.from = from
            return this
        }

        fun setPacketType(packetType: String): Builder {
            this.packetType = packetType
            return this
        }

        fun setCommand(command: String): Builder {
            this.command = command
            return this
        }

        fun setAction(action: String): Builder {
            this.action = action
            return this
        }

        fun setRemark(remark: String): Builder {
            this.remark = remark
            return this
        }

        fun build(): CommandDatagram {
            return CommandDatagram(deviceId, from, packetType, command, action, remark)
        }
    }

    fun toJsonString(): String {
        val jsonObject = JSONObject()
        jsonObject.put("device_id", deviceId)
        jsonObject.put("from", from)
        jsonObject.put("packet_type", packetType)
        jsonObject.put("command", command)
        jsonObject.put("action", action)
        jsonObject.put("remark", remark)
        return jsonObject.toString()
    }
}
