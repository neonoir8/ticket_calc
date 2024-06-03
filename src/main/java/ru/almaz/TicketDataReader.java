package ru.almaz;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class TicketDataReader {
    public static List<Ticket> readTickets(String filePath, String origin, String destination) throws IOException {
        List<Ticket> tickets = new ArrayList<>();
        String content = readFile(filePath);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(content);
        JsonNode ticketsNode = rootNode.get("tickets");

        for (JsonNode ticketNode : ticketsNode) {
            Ticket ticket = mapper.treeToValue(ticketNode, Ticket.class);
            if (ticket.getOrigin().equals(origin) && ticket.getDestination().equals(destination)) {
                tickets.add(ticket);
            }
        }
        return tickets;
    }

    private static String readFile(String filePath) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(filePath)) {
            if (inputStream == null) {
                throw new IOException("Файл не найден: " + filePath);
            }
            return new String(inputStream.readAllBytes());
        }
    }
}
