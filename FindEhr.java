import java.io.*;
import java.util.*;

public class FindEhr
{
  public static void main(String[] args) throws IOException, java.lang.InterruptedException
  {
    int k = 0;
    final int arraySize = 20;
    File outFile = new File("dump.dat");
    FileWriter out = new FileWriter(outFile);

    File aboutFile = new File("ab.dat");
    FileWriter about = new FileWriter(aboutFile);


    File inFile = new File("inPoints.dat");
    FileReader in = new FileReader(inFile);
    BufferedReader br2 = new BufferedReader(in);
    String s = new String(br2.readLine());

    while(br2.ready())
    {
      k++;
      if(k%100 == 0)
      {
        System.out.println("starting loop number " + k);
      }
      ArrayList x = new ArrayList(arraySize);
      ArrayList y = new ArrayList(arraySize);
      ArrayList z = new ArrayList(arraySize);
      ArrayList w = new ArrayList(arraySize);

      StringTokenizer sTx = new StringTokenizer(new String(br2.readLine()));
      StringTokenizer sTy = new StringTokenizer(new String(br2.readLine()));
      StringTokenizer sTz = new StringTokenizer(new String(br2.readLine()));
      StringTokenizer sTw = new StringTokenizer(new String(br2.readLine()));
      int n = sTw.countTokens();
      for(int i=0; i<n; i++)
      {
        x.add(i,sTx.nextToken());
        y.add(i,sTy.nextToken());
        z.add(i,sTz.nextToken());
        w.add(i,sTw.nextToken());
      }

      //find the Erhart polynomial for the tetrahedron
      double a,b,c,d,e;

      a = 0;
      b = 0;
      c = 0;
      d = 0;
      e = 1;

      File outFile2 = new File("ex.txt");
      FileWriter out2 = new FileWriter(outFile2);

      out2.write(Integer.toString(n+1) + " " + Integer.toString(7) + '\n');
      out2.write('\n');
      for(int i=0; i<n; i++)
      {
        out2.write(Integer.toString(1) + " " + (String)(x.get(i))
                                     + " " + (String)(y.get(i))    
                                     + " " + (String)(z.get(i))    
                                     + " " + (String)(w.get(i))    
                                     + " " + Integer.toString(1)      
                                     + " " + Integer.toString(0) + '\n');
      }
      out2.write(Integer.toString(1) + " " + Integer.toString(0)
                                     + " " + Integer.toString(0)      
                                     + " " + Integer.toString(0)
                                     + " " + Integer.toString(0)
                                     + " " + Integer.toString(0)      
                                     + " " + Integer.toString(1) + '\n');
      out2.write('\n');
      out2.write(Integer.toString(2) + " " + Integer.toString(3) + '\n');
      out2.write(Integer.toString(1) + " " + Integer.toString(1) 
                                     + " " + Integer.toString(0) + '\n');
      out2.write(Integer.toString(1) + " " + Integer.toString(0) 
                                     + " " + Integer.toString(1) + '\n');
      
      out2.close();

      String[] polyLibcmd = {"sh", "-c", "../polylib-5.20.0/prog2.exe < ex.txt > ehr.out"};
      Process proc = Runtime.getRuntime().exec(polyLibcmd);

      int exitVal = proc.waitFor();

      //parse the output file
      File inputFile = new File("ehr.out");
      FileReader inRed = new FileReader(inputFile);   
      BufferedReader br = new BufferedReader(inRed);
      int numVertices = 0;
      boolean f = true;
      if(!br.ready())
      {
        out.close();
        break;
      }
      while(f)
      {
        s = br.readLine();
        if(s.length() > 0)
        {
          if(s.charAt(0) == 'E')
          {
            f = false;
          }
        }
      }

      StringTokenizer sT = new StringTokenizer(br.readLine(), "(* )+");
      
    while(sT.hasMoreTokens())
    {
      double temp;
      StringTokenizer sT2 = new StringTokenizer(sT.nextToken(), "/");
      Integer intA = new Integer(sT2.nextToken());
      temp = intA.intValue();
      if(sT2.hasMoreTokens())
      {
        intA = new Integer(sT2.nextToken());
        temp = temp/intA.intValue();
      }

      String ss;
      if(sT.hasMoreTokens())
      {
        ss = sT.nextToken();
        if(ss.equals(new String("P^4")))
        {
          a = temp;
        }
        if(ss.equals(new String("P^3")))
        {
          b = temp;
        }
        if(ss.equals(new String("P^2")))
        {
          c = temp;
        }
        if(ss.equals(new String("P")))
        {
          d = temp;
        }
      }
      else
      {
        e = temp;
      }
    }

      File outFile3 = new File("mapleIn.txt");
      FileWriter out3 = new FileWriter(outFile3);

      out3.write("Digits:=5:"+'\n');

      out3.write("a:=solve(" + a + "*t^4");
      if(b < 0)
      {
        out3.write(b + "*t^3");
      }
      else
      {
        out3.write("+" + b + "*t^3");
      }
      if(c < 0)
      {
        out3.write(c + "*t^2");
      }
      else
      {
        out3.write("+" + c + "*t^2");
      }
      if(d < 0)
      {
        out3.write(d + "*t");
      }
      else
      {
        out3.write("+" + d + "*t");
      }
      out3.write("+1,t):" + '\n');
      out3.write("Re(a[1]);" + '\n');
      out3.write("Im(a[1]);" + '\n');
      out3.write("Re(a[2]);" + '\n');
      out3.write("Im(a[2]);" + '\n');
      out3.write("Re(a[3]);" + '\n');
      out3.write("Im(a[3]);" + '\n');
      out3.write("Re(a[4]);" + '\n');
      out3.write("Im(a[4]);" + '\n');
      
      out3.close();

      String[] maplecmd = {"sh", "-c", "maple < mapleIn.txt > mapleOut.txt"};
      proc = Runtime.getRuntime().exec(maplecmd);

      exitVal = proc.waitFor();
//      System.out.println("Exit value of: " + exitVal);
      

      //find the roots of the Erhart polynomial
      float r1,r2,r3,r4,i1,i2,i3,i4;

      inputFile = new File("mapleOut.txt");
      in = new FileReader(inputFile);   
      br = new BufferedReader(in);
      for(int j=0; j<8; j++)
      {
        br.readLine();
      }
      String h;
      boolean flag = false;
      h = new String(br.readLine());
      sT = new StringTokenizer(h+'0',".");
      sT.nextToken();
      if(!sT.hasMoreTokens())
      {
        h = br.readLine();
        flag = true;
      }
      sT= new StringTokenizer(h);
      h = sT.nextToken();
      Float t = new Float(h+'0');
      r1 = t.floatValue();
      if(flag)
      {
        r1 = 0;
        flag = false;
      }
      br.readLine();
      br.readLine();
      
      h = new String(br.readLine());
      sT = new StringTokenizer(h+'0',".");
      sT.nextToken();
      if(!sT.hasMoreTokens())
      {
        h = br.readLine();
        flag = true;
      }
      sT= new StringTokenizer(h);
      h = sT.nextToken();
      t = new Float(h+'0');
      i1 = t.floatValue();
      if(flag)
      {
        i1 = 0;
        flag = false;
      }
      br.readLine();
      br.readLine();

      h = new String(br.readLine());
      sT = new StringTokenizer(h+'0',".");
      sT.nextToken();
      if(!sT.hasMoreTokens())
      {
        h = br.readLine();
        flag = true;
      }
      sT= new StringTokenizer(h);
      h = sT.nextToken();
      t = new Float(h+'0');
      r2 = t.floatValue();
      if(flag)
      {
        r2 = 0;
        flag = false;
      }
      br.readLine();
      br.readLine();

      h = new String(br.readLine());
      sT = new StringTokenizer(h+'0',".");
      sT.nextToken();
      if(!sT.hasMoreTokens())
      {
        h = br.readLine();
        flag = true;
      }
      sT= new StringTokenizer(h);
      h = sT.nextToken();
      t = new Float(h+'0');
      i2 = t.floatValue();
      if(flag)
      {
        i2 = 0;
        flag = false;
      }
      br.readLine();
      br.readLine();

      h = new String(br.readLine());
      sT = new StringTokenizer(h+'0',".");
      sT.nextToken();
      if(!sT.hasMoreTokens())
      {
        h = br.readLine();
        flag = true;
      }
      sT= new StringTokenizer(h);
      h = sT.nextToken();
      t = new Float(h+'0');
      r3 = t.floatValue();
      if(flag)
      {
        r3 = 0;
        flag = false;
      }
      br.readLine();
      br.readLine();

      h = new String(br.readLine());
      sT = new StringTokenizer(h+'0',".");
      sT.nextToken();
      if(!sT.hasMoreTokens())
      {
        h = br.readLine();
        flag = true;
      }
      sT= new StringTokenizer(h);
      h = sT.nextToken();
      t = new Float(h+'0');
      i3 = t.floatValue();
      if(flag)
      {
        i3 = 0;
        flag = false;
      }
      br.readLine();
      br.readLine();

      h = new String(br.readLine());
      sT = new StringTokenizer(h+'0',".");
      sT.nextToken();
      if(!sT.hasMoreTokens())
      {
        h = br.readLine();
        flag = true;
      }
      sT= new StringTokenizer(h);
      h = sT.nextToken();
      t = new Float(h+'0');
      r4 = t.floatValue();
      if(flag)
      {
        r4 = 0;
        flag = false;
      }
      br.readLine();
      br.readLine();

      h = new String(br.readLine());
      sT = new StringTokenizer(h+'0',".");
      sT.nextToken();
      if(!sT.hasMoreTokens())
      {
        h = br.readLine();
        flag = true;
      }
      sT= new StringTokenizer(h);
      h = sT.nextToken();
      t = new Float(h+'0');
      i4 = t.floatValue();
      if(flag)
      {
        i4 = 0;
        flag = false;
      }

      //output the roots of the Erhart polynomial

      out.write(Float.toString(r1) + " " + Float.toString(i1)
                + " # (" + x.get(0) + ", " + y.get(0) + ", " + z.get(0) +  "," + w.get(0) + "), "
                + " (" + x.get(1) + ", " + y.get(1) + ", " + z.get(1) +  "," + w.get(1)+ "), "
                + " (" + x.get(2) + ", " + y.get(2) + ", " + z.get(2) +  "," + w.get(2)+ "), "
                + " (" + x.get(3) + ", " + y.get(3) + ", " + z.get(3) +  "," + w.get(3)+ "), "
                + " (" + x.get(4) + ", " + y.get(4) + ", " + z.get(4) +  "," + w.get(4)+ "), "
                + " (" + x.get(5) + ", " + y.get(5) + ", " + z.get(5) +  "," + w.get(5)+ ")" + '\n');


      out.write(Float.toString(r2) + " " + Float.toString(i2) 
                + " # " 
                + a + ", "
                + b + ", "
                + c + ", "
                + d + ", "
                + e + '\n');
      out.write(Float.toString(r3) + " " + Float.toString(i3) + '\n');
      out.write(Float.toString(r4) + " " + Float.toString(i4) + '\n');

      double aHat = a+b+c+d-4;
      double bHat = 11*a+3*b-c-3*d+6;

      about.write(aHat + " " + bHat + '\n');

      s = br2.readLine();
    }

    out.close();
    about.close();

  }
}
