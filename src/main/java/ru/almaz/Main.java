package ru.almaz;

public class Main {

    public static void main(String[] args) {
        String filePath = "tickets.json";
        String origin = "VVO";
        String destination = "TLV";
        TicketCalculator ticketCalculator = new TicketCalculator(filePath, origin, destination);
        ticketCalculator.analyzeTickets();
    }
}
