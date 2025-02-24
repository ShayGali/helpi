const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

exports.sendChatNotification = functions
    .database.ref("/chats/{chatId}/messages/{messageId}")
    .onCreate(async (snapshot, context) => {
        const message = snapshot.val();
        const chatId = context.params.chatId;

        console.log("Message data:", message);
        console.log("Chat ID:", chatId);

        if (!message || !message.receiverId || !message.senderId || !message.message) {
            console.error("Invalid message data", message);
            return null;
        }

        try {
            // Get recipient token from Firestore
            const recipientDoc = await admin
                .firestore()
                .collection("users")
                .doc(message.receiverId)
                .get();

            if (!recipientDoc.exists) {
                console.error("Recipient not found");
                return null;
            }

            const recipientData = recipientDoc.data();

            if (recipientData.notificationEnabled === false) {
                console.log("Notifications disabled for recipient. Skipping notification.");
                return null;
            }

            const recipientToken = recipientData.fcmToken;

            if (!recipientToken) {
                console.error("No FCM token found for recipient");
                return null;
            }

            // Get sender data from Firestore
            const senderDoc = await admin
                .firestore()
                .collection("users")
                .doc(message.senderId)
                .get();

            if (!senderDoc.exists) {
                console.error("Sender not found");
                return null;
            }

            const senderData = senderDoc.data();
            const senderName = `${senderData.firstName} ${senderData.lastName}`;

            // Create message using FCM V1 API format
            const fcmMessage = {
                message: {
                    token: recipientToken,
                    data: {
                        chatId: chatId,
                        message: message.message,
                        senderName: senderName,
                        type: "new_message",
                        timestamp: String(message.timestamp || Date.now())
                    },
                    notification: {
                        title: senderName,
                        body: message.message
                    },
                    android: {
                        priority: "high",
                        notification: {
                            channelId: "chat_messages",
                            priority: "high",
                            defaultSound: true,
                            clickAction: "FLUTTER_NOTIFICATION_CLICK"
                        }
                    }
                }
            };

            // Send message using v1 API
            const response = await admin.messaging().send(fcmMessage.message);
            console.log("Successfully sent message:", response);
            return { success: true };

        } catch (error) {
            console.error("Error sending message:", error);
            return { success: false, error: error.message };
        }
    });