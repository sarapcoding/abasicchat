package controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import model.ChatMessage;

@Component
public class WebSocketEventListener {

	private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);
	
	@Autowired
	private SimpMessageSendingOperations messagingTemplate;
	
	// Cuando se recibe una nueva conexión, se avisa de ello
	
	@EventListener
	public void handleWebSocketConectListener(SessionConnectedEvent event) {
		logger.info("Se ha recibido una nueva conexión en el web socket");
	}
	
	//Cuando un usario se desconecta, se informa de ello
	
	@EventListener
	public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
		
		String username = (String) headerAccessor.getSessionAttributes().get("username");
		
		if(username != null) {
			logger.info("Usuario desconectado : "+username);
			
			ChatMessage chatMessage = new ChatMessage();
			chatMessage.setType(ChatMessage.MessageType.LEAVE);
			chatMessage.setSender(username);
			
			messagingTemplate.convertAndSend("/topic/public",chatMessage);
		}
	}
}
