import java.util.Scanner;

class Unode {
    Unode llink;
    int data;
    Unode rlink;

    public Unode(int data) {
        this.data = data;
        this.llink = this.rlink = null;
    }
}

class Rnode {
    Rnode llink;
    int data;
    Rnode rlink;
    Unode child;

    public Rnode(int data) {
        this.data = data;
        this.llink = this.rlink = null;
        this.child = null;
    }
}

public class DLL {
    public static Rnode insertBegin(Rnode head, int data) {
        Rnode temp = new Rnode(data);
        if (head == null) {
            head = temp;
            return head;
        }
        head.llink = temp;
        temp.rlink = head;
        head = temp;
        return head;
    }

    public static Rnode insertEnd(Rnode head, int data) {
        Rnode temp = new Rnode(data);
        if (head == null) {
            head = temp;
            return head;
        }
        Rnode cur = head;
        while (cur.rlink != null) {
            cur = cur.rlink;
        }
        cur.rlink = temp;
        temp.llink = cur;
        return head;
    }

    public static Rnode insertUnode(Rnode head, int data, int resource) {
        Unode temp = new Unode(data);

        Rnode cur = head;
        while (cur != null && cur.data != resource) {
            cur = cur.rlink;
        }
        if (cur == null) {
            System.out.println("Resource node not found.");
            return head;
        }
        if (cur.child == null) {
            cur.child = temp;
        } else {
            Unode uCur = cur.child;
            while (uCur.rlink != null) {
                uCur = uCur.rlink;
            }
            uCur.rlink = temp;
            temp.llink = uCur;
        }
        return head;
    }

    public static Rnode deleteUnode(Rnode head, int data, int resource) {
        Rnode cur = head;

        while (cur != null && cur.data != resource) {
            cur = cur.rlink;
        }
        if (cur == null) {
            System.out.println("Resource node not found.");
            return head;
        }

        Unode uCur = cur.child;
        if (uCur == null) {
            System.out.println("No child nodes to delete.");
            return head;
        }

        while (uCur != null && uCur.data != data) {
            uCur = uCur.rlink;
        }
        if (uCur == null) {
            System.out.println("Unode with data " + data + " not found.");
            return head;
        }

        if (uCur.llink != null) {
            uCur.llink.rlink = uCur.rlink;
        } else {
            cur.child = uCur.rlink;
        }

        if (uCur.rlink != null) {
            uCur.rlink.llink = uCur.llink;
        }

        return head;
    }

    public static Rnode deleteBegin(Rnode head) {
        if (head == null) {
            System.out.println("DLL is empty");
            return head;
        }
        Rnode temp = head;
        head = head.rlink;
        if (head != null) {
            head.llink = null;
        }
        return head;
    }

    public static Rnode deleteEnd(Rnode head) {
        if (head == null) {
            System.out.println("DLL is empty");
            return head;
        }
        Rnode cur = head;
        while (cur.rlink != null) {
            cur = cur.rlink;
        }
        if (cur.llink != null) {
            cur.llink.rlink = null;
        } else {
            head = null;
        }
        return head;
    }

    public static void print(Rnode head) {
        Rnode rCur = head;
        while (rCur != null) {
            System.out.print(rCur.data);
            Unode uCur = rCur.child;
            while (uCur != null) {
                System.out.print("->" + uCur.data);
                uCur = uCur.rlink;
            }
            System.out.println();
            rCur = rCur.rlink;
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Rnode head = null;
        int choice, ele, resource;

        while (true) {
            System.out.println("1. Insert begin \n2. Insert end\n3. Insert unode\n4. Print\n5. Delete begin\n6. Delete end\n7. Delete unode\n8. Exit");
            choice = sc.nextInt();
            switch (choice) {
                case 1:
                    System.out.println("Enter data:");
                    ele = sc.nextInt();
                    head = insertBegin(head, ele);
                    break;
                case 2:
                    System.out.println("Enter data:");
                    ele = sc.nextInt();
                    head = insertEnd(head, ele);
                    break;
                case 3:
                    System.out.println("Enter the resource (rnode data) and unode data:");
                    resource = sc.nextInt();
                    ele = sc.nextInt();
                    head = insertUnode(head, ele, resource);
                    break;
                case 4:
                    print(head);
                    break;
                case 5:
                    head = deleteBegin(head);
                    break;
                case 6:
                    head = deleteEnd(head);
                    break;
                case 7:
                    System.out.println("Enter the resource (rnode data) and unode data to delete:");
                    resource = sc.nextInt();
                    ele = sc.nextInt();
                    head = deleteUnode(head, ele, resource);
                    break;
                case 8:
                    sc.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice");
                    break;
            }
        }
    }
}
