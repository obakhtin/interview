import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Sync {

    BillingService billingService = new BillingService();


    @Test
    public void stepByStep() {
        Client petya1 = new Client("Petya");
        Client petya2 = new Client("Petya");

        billingService.deposit(petya1, 100);
        billingService.deposit(petya2, 200);

        billingService.withdraw(petya1, 200);
        billingService.withdraw(petya2, 100);

        assertEquals(0, billingService.getBalance(petya1));
        assertEquals(0, billingService.getBalance(petya2));
    }

    @Test
    public void async() {
        final Client petya1 = new Client("Petya");
        final int amount = 100;
        final int count = 1000;

        ExecutorService threadPool = Executors.newFixedThreadPool(100);

        IntStream.range(0, count)
                .mapToObj(i -> threadPool.submit(() -> billingService.deposit(petya1, amount)))
                .collect(Collectors.toList())
                .forEach(future -> {
                    try {
                        future.get();
                    }
                    catch (Throwable e) {
                        e.printStackTrace();
                    }
                });

        assertEquals(count * amount, billingService.getBalance(petya1));

        IntStream.range(0, count)
                .mapToObj(i -> threadPool.submit(() -> billingService.withdraw(petya1, count)))
                .collect(Collectors.toList()).forEach(future -> {
            try {
                future.get();
            }
            catch (Throwable e) {
                e.printStackTrace();
            }
        });

        assertEquals(0, billingService.getBalance(petya1));
        threadPool.shutdown();
    }

}


class BillingService {

    private final Map<String, Integer> bankAccounts = new HashMap<>();

    /**
     * положить на счет
     */
    void deposit(Client client, Integer amount) {
        Integer currentAmount = getBalance(client);
        if (currentAmount != null) {
            bankAccounts.put(client.getName(), currentAmount + amount);
        }
        else {
            bankAccounts.put(client.getName(), amount);

        }
    }


    /**
     * снять со счета
     */
    void withdraw(Client client, Integer amount) {
        Integer currentAmount = getBalance(client);
        if (currentAmount != null && currentAmount >= amount) {
            bankAccounts.put(client.getName(), currentAmount - amount);
        }
    }

    /**
     * запросить баланс
     */
    Integer getBalance(Client client) {
        return bankAccounts.get(client.getName());
    }

}

class Client {

    private String name;

    Client(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}