import java.util.Scanner;
public class Design {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        String a = input.next();
        String b = input.next();
        int c = b.length();
        if (c != 8) {
            System.out.println(b);
        } else {
            String d = "";
            for (int i = 0; i < 3; i++) {
                d += b.charAt(i);
            }
            int e = Integer.parseInt(d);
            if (e >= 115 && e <= 120) {
                System.out.println(a + ", welcome to Baoneng City!");
            } else {
                System.out.println(b);
            }
        }
    }
}