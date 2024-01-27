public class Example {
    public static void main(String[] args) {
        Long c = System.currentTimeMillis();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            
        }
        System.out.println(c-System.currentTimeMillis());
    }
}
