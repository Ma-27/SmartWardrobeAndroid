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
    private val target: Int? = null,
    private val packetType: String,
    private val command: String,
    private val actuator: String? = null, // 改为可空类型
    private val action: String,
    private val remark: String,
) {
    class Builder {
        private var deviceId: Int = 1
        private var from: Int = 0
        private var packetType: String = "Command"
        private var command: String = "Default"
        private var action: String = "default"
        private var remark: String = "default remark"

        private var target: Int? = null // Making target nullable in the builder
        private var actuator: String? = null

        fun setTarget(target: Int?): Builder { // Allow setting target as nullable
            this.target = target
            return this
        }

        fun setActuator(actuator: String?): Builder {
            this.actuator = actuator
            return this
        }

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
            return CommandDatagram(
                deviceId,
                from,
                target,
                packetType,
                command,
                actuator ?: "",
                action,
                remark
            )
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
        // 添加target字段，只要非 null
        target?.let { jsonObject.put("target", it) }
        // 添加actuator字段，只要非 null
        actuator?.let { jsonObject.put("actuator", it) }
        return jsonObject.toString()
    }
}
