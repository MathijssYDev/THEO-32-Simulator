import theo32machine.THEO32;

public class Main {
    public static void main(String args[]) throws Exception{
        if (args.length != 3) throw new Exception("(Main Thread) Missing Arguments! Expected arguments: File location ROM, File location Bootloader, COMPORT to arduino (AY-3-8910)");

        THEO32 theo32 = new THEO32("ROM/"+args[0],"ROM/" +args[1],Integer.parseInt(args[2]),1000);
        theo32.start();
    }
}