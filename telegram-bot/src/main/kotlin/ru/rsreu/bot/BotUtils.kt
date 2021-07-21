package ru.rsreu.bot

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.CallbackQuery
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ReplyMarkup

fun Bot.sendMsg(chatId: Long, text: String, buttons: ReplyMarkup? = null) =
    this.sendMessage(
        chatId = ChatId.fromId(chatId),
        text = text,
        replyMarkup = buttons
    )

fun Bot.sendSomeMsg(chatId: Long, vararg texts: String) {
    texts.forEach {
        sendMsg(chatId, it)
    }
}

fun Bot.deleteLastMessage(callbackQuery: CallbackQuery) {
    this.deleteMessage(
        chatId = ChatId.fromId(callbackQuery.from.id),
        messageId = callbackQuery.message!!.messageId
    )
}