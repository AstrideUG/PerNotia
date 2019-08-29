package me.helight.pernotia.logging;

import me.helight.pernotia.PerNotia;

public class Log {

    private boolean finished = false;

    public void start() {
        System.out.println(PerNotia.HEADER);

        PerNotia.POOL.execute(() -> {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (!finished) {
                finish();
            }
        });
    }

    public void print(String... strings) {
        for (String string : strings) {
            System.out.println(string);
        }
    }

    public void finish() {
        finished = true;
        System.out.println(PerNotia.FOOTER);
    }
}
