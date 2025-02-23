const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

// Listen for new messages
exports.sendChatNotification = functions
    .database.ref("/chats/{chatId}/messages/{messageId}")
    .onCreate(async (snapshot, context) => {
        const message = snapshot.val();
        const chatId = context.params.chatId;

        // Log for debugging
        console.log("New message created:", message);
        console.log("Chat ID:", chatId);

        // Validate message data
        if (!message || !message.receiverId || !message.senderId || !message.message) {
            console.error("Invalid message data:", message);
            return null;
        }

        try {
            // Get recipient user data from Firestore
            const recipientSnapshot = await admin
                .firestore()
                .collection("users")
                .doc(message.receiverId)
                .get();

            const recipientData = recipientSnapshot.data();
            if (!recipientData || !recipientData.fcmToken) {
                console.log("No recipient FCM token found for user:", message.receiverId);
                return null;
            }

            // Get sender user data from Firestore
            const senderSnapshot = await admin
                .firestore()
                .collection("users")
                .doc(message.senderId)
                .get();

            const senderData = senderSnapshot.data();
            if (!senderData) {
                console.error("Sender data not found:", message.senderId);
                return null;
            }

            // Construct the sender's full name
            const senderName = `${senderData.firstName} ${senderData.lastName}`;

            // Create notification payload
            const payload = {
                notification: {
                    title: senderName,
                    body: message.message,
                    clickAction: "FLUTTER_NOTIFICATION_CLICK",
                    sound: "default",
                },
                data: {
                    chatId: chatId,
                    message: message.message,
                    senderName: senderName,
                    type: "new_message",
                    senderId: message.senderId,
                    timestamp: message.timestamp.toString(),
                },
            };

            // Send notification
            const response = await admin.messaging().sendToDevice(
                recipientData.fcmToken,
                payload,
            );

            // Log the results
            console.log("Notification sent successfully:", response);

            // Handle failures
            const failureCount = response.failureCount;
            if (failureCount > 0) {
                response.results.forEach((result) => {
                    if (!result.success) {
                        console.error("Notification failed to send:", result.error);
                    }
                });
            }

            return {success: true, response: response};

        } catch (error) {
            console.error("Error sending notification:", error);
            return {success: false, error: error.message};
        }
    });