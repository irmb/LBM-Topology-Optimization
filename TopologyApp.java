/*
 * SPDX-FileCopyrightText: 2023 Martin Geier <mailto:geier(at)irmb.tu-bs.de>
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.util.StringTokenizer;
public class TopologyApp extends JFrame {
    // Assuming you have a SolverThread class inside your applet
    private SolverThread solver;

    private JLabel statusLabel;

    /*static*/ int smax=1000000000;

    /*static*/ int xmax=150;
    /*static*/	///*static*/ final float visscale= 1.0e2f;
    /*static*/ int ymax=50;
    /*static*/ //int inflow=  1100000;
    /*static*/ int outflow= 50000000;
    /*static*/ int penColor=1;
    /*static*/ float om=1.99f;//1.259999f;
    /*static*/ float vsc=20;
    /*static*/ float sc=5;
    /*static*/ int sz=1;
    /*static*/ int t=0;
    /*static*/ int[][][] dat;
    /*static*/ float[][][] v;
    //float[][][] ob;
    float[][] alpha;
    float[][] dalpha;
    float vx=0;
    float obom=0;
    int vp=1;

    JLabel imtek;//<img src='http://www.imtek.de/~geier/icons/imtekLoop.gif' alt='www.imtek.de'></html>");//(iconImtek);
    JSlider vis;
    JSlider pres;
    JSlider step;
    JSlider scaler;
    JPanel control;
    JPanel subcontrol;
    JPanel butControl;
    JPanel dispControl;
    JLabel visShow;
    ButtonGroup drawControl;
    JRadioButton drawBut;
    JRadioButton ereaseBut;
    JRadioButton probBut;
    JRadioButton voidBut;
    JCheckBox colorToc;
    JCheckBox clock;
    Display display;
    JPanel optControl;
    JSlider optOmS;
    JSlider optAS;
    //JSlider optMS;
    JSlider optPowS;
    JSlider optExpS;
    float optExp;
    //float optM;
    float optOm;
    double optA;
    double optSc;
    int optS;//, optPow;
    double q;
    long start;
    int time;


    public TopologyApp() {
        initUI();

        solver = new SolverThread();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (solver != null) {
                    solver.interrupt();
                }
            }
        });
        setTitle("Topology Application");
        // setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void initUI() {
        {
            obom=0;
            q=0;
            // String x=getParameter("x");
            // String y=getParameter("y");
            String x="200";
            String y="100";

            if (x!=null){xmax=Integer.parseInt(x);}
            if (y!=null){ymax=Integer.parseInt(y);}
        }
        // optOm=0;
        // optA=0;
        optS = 0;
        // optPow=1;
        // optM=1;
        optSc = 0;
        optExp = 0.0f;
        // isr=new InputStreamReader(System.in);
        // input=new BufferedReader(isr);
        // io=new IOThread();

        imtek = new JLabel("<html><center><h1>www.imtek.de</h1></center></html>");// <img
                                                                                  // src='http://www.imtek.de/~geier/icons/imtekLoop.gif'
                                                                                  // alt='www.imtek.de'></html>");//(iconImtek);
        vis = new JSlider(JSlider.HORIZONTAL, 10, smax / 2, (int) (smax / 3. * (1. / om - 0.5)));
        pres = new JSlider(JSlider.HORIZONTAL, -1000, 1000, 0);
        step = new JSlider(JSlider.HORIZONTAL, 1, 200, sz);
        scaler = new JSlider(JSlider.HORIZONTAL, 1, 300, (int) vsc);
        control = new JPanel();
        subcontrol = new JPanel();
        butControl = new JPanel();
        dispControl = new JPanel();
        visShow = new JLabel("kinematic viscosity=" + Float.toString(1.f / 3.f * (1.f / om - 0.5f)), JLabel.CENTER);
        drawControl = new ButtonGroup();
        drawBut = new JRadioButton("draw");
        ereaseBut = new JRadioButton("erase");
        probBut = new JRadioButton("probe");
        voidBut = new JRadioButton("void");
        colorToc = new JCheckBox("color");
        clock = new JCheckBox("clock");

        display = new Display();

        optControl=new JPanel();
        optOmS=new JSlider(JSlider.VERTICAL,0,500,0);
        optAS=new JSlider(JSlider.VERTICAL,0,xmax*ymax,0);
        //optMS=new JSlider(JSlider.VERTICAL,0,999,0);
        optPowS=new JSlider(JSlider.VERTICAL,0,1000,(int)(obom*1000));
        optExpS=new JSlider(JSlider.VERTICAL,0,100,(int)(optExp*100));
        // set the default look and feel
        String laf = UIManager.getSystemLookAndFeelClassName();
        try {
            UIManager.setLookAndFeel(laf);
        } catch (UnsupportedLookAndFeelException exc) {
            System.err.println ("Warning: UnsupportedLookAndFeel: " + laf);
        } catch (Exception exc) {
            System.err.println ("Error loading " + laf + ": " + exc);
        }
        Container cp=getContentPane();
        //Container cp=new Container();

        vis.addChangeListener(new ChangeListener(){

            public void stateChanged(ChangeEvent e){
                int val=((JSlider)e.getSource()).getValue();
                om=1.f/(3.f*val/smax+0.5f);
                //om=2.0f*(1.0f-(float)Math.exp((float)val/((float)smax))/(float)Math.exp(1.0));
                float nu=1.f/3.f*(1.f/om-0.5f);
                showStatus("New kinematic Viscosity="+Float.toString(nu));
                visShow.setText("kinematic viscosity="+Float.toString(1.f/3.f*(1.f/om-0.5f)));
                visShow.repaint();
            }});
        pres.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e){
                vx=((JSlider)e.getSource()).getValue()/50000.0f;
                vp=(vx>0) ? 1 : 0;
                //float dp=(float)inflow/((float)outflow);
                showStatus("New flow speed="+Float.toString(vx));
                //vx=dp-1;//(2.0f*inflow)/(inflow+outflow);
            }
        });
        step.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e){
                sz=((JSlider)e.getSource()).getValue();

                showStatus(Integer.toString(sz)+" time steps between display update");
            }
        });
        //b1.addActionListener(a1);
        //b1.setToolTipText("press");
        scaler.addChangeListener(new ChangeListener(){public void stateChanged(ChangeEvent e){
            vsc=((JSlider)e.getSource()).getValue();
        }});


        optOmS.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e){
                int val=((JSlider)e.getSource()).getValue();
                optSc=val/500.f;
                showStatus("New rate of change="+Float.toString((float)optSc));
                //optSc=Math.log(1-optSc);
            }});

        optAS.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e){
                optS=((JSlider)e.getSource()).getValue();

                showStatus("New number of solid sites="+Integer.toString(optS));
            }});


				/*optMS.addChangeListener(new ChangeListener(){
				public void stateChanged(ChangeEvent e){
				 optM=1-(((JSlider)e.getSource()).getValue()/1000.f);

				showStatus("Maximal poreausity="+Float.toString(1-optM));
				}});*/

        optPowS.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e){
                obom=(((JSlider)e.getSource()).getValue());
                obom=obom/1001.0f;
                showStatus("Lowpass coefficient="+Float.toString(obom));
            }});

        optExpS.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e){
                optExp=(((JSlider)e.getSource()).getValue())/100.f;
                showStatus("Rate of momentum exchange="+Float.toString(optExp));
            }});

        drawBut.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){penColor=1;}
        });
        ereaseBut.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){penColor=0;}
        });
        probBut.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){penColor=2;}
        });
        voidBut.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){penColor=3;}
        });
        clock.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if (clock.isSelected()){start=System.currentTimeMillis();time=t;}
                else {start=System.currentTimeMillis()-start;time=t-time;}
            }
        });
        vis.setToolTipText("Adjust viscosity");
        pres.setToolTipText("Adjust inflow speed");
        step.setToolTipText("Set number of time steps between display update");
        scaler.setToolTipText("Adjust scaling");
        optOmS.setToolTipText("Adjust rate of change");
        optAS.setToolTipText("Adjust solid fraction");
        //optMS.setToolTipText("Adjust poreausity");
        optExpS.setToolTipText("Adjust rate of momentum exchange with solid");
        optPowS.setToolTipText("Adjust lowpass coefficient");
        subcontrol.setLayout (new FlowLayout());

        control.setLayout(new BorderLayout());
        //cp.add(b1);
        //cp.add(b2);
        //int w=vis.getPreferredSize().height;
        //vis.setPreferredSize(new Dimension(300,w));
        control.add(BorderLayout.NORTH,vis);
        control.add(BorderLayout.CENTER,pres);
        drawControl.add(drawBut);
        drawBut.setEnabled(true);
        drawControl.add(ereaseBut);
        drawControl.add(voidBut);
        drawControl.add(probBut);
        //subcontrol.add(imtek);
        //subcontrol.add(colorToc);
        //butControl.setLayout(new BorderLayout());
        butControl.setLayout(new BoxLayout(butControl,BoxLayout.Y_AXIS));
        butControl.add(colorToc);
        butControl.add(drawBut);
        butControl.add(ereaseBut);
        butControl.add(voidBut);
        butControl.add(probBut);
        //butControl.add(BorderLayout.NORTH,drawBut);
        //butControl.add(BorderLayout.CENTER,ereaseBut);
        //butControl.add(BorderLayout.SOUTH,voidBut);
        //butControl.add(BorderLayout.EAST,probBut);
        subcontrol.add(butControl);
        //dispControl.setLayout(new BorderLayout());
        //dispControl.add(BorderLayout.WEST,step);
        //dispControl.add(BorderLayout.EAST,scaler);
        //dispControl.add(BorderLayout.NORTH,imtek);
        //dispControl.add(BorderLayout.SOUTH,visShow);
        dispControl.setLayout(new BoxLayout(dispControl,BoxLayout.Y_AXIS));
        dispControl.add(imtek);
        dispControl.add(step);
        dispControl.add(scaler);
        dispControl.add(clock);
        //dispControl.add(visShow);
        subcontrol.add(dispControl);
        //subcontrol.add(drawControl);//
        control.add(BorderLayout.SOUTH,subcontrol);
        display.setPreferredSize(new Dimension(1000,600));
        display.setBackground(Color.white);
        //	optControl.setLayout(new BorderLayout());
        //	optControl.add(BorderLayout.WEST,optOmS);
        //	optControl.add(BorderLayout.CENTER,optAS);
        optControl.setLayout(new BoxLayout(optControl,BoxLayout.X_AXIS));
        optControl.add(optOmS);
        optControl.add(optAS);
        optControl.add(optExpS);
        optControl.add(optPowS);

        dat=new int[xmax][ymax][10]; //r nw w sw s se e ne n id
        v=new float[xmax][ymax][2];
        alpha=new float[xmax][ymax];
        dalpha=new float[xmax][ymax];
        //ob=new float[xmax][ymax][2];
        for (int xi=1; xi<xmax; xi++){

            for (int yi=0;yi<ymax; yi++){
                dat[xi][yi][1]=dat[xi][yi][3]=dat[xi][yi][5]=dat[xi][yi][7]=outflow;
                dat[xi][yi][2]=dat[xi][yi][4]=dat[xi][yi][6]=dat[xi][yi][8]=4*outflow;
                dat[xi][yi][0]=16*outflow;

            }
            //start=System.currentTimeMillis();
        }
		/*
		// for Zhenyu
		for (int xi=1; xi<xmax;xi++){dat[xi][0][9]=1;dat[xi][ymax-1][9]=1;}

		int length=(int)(xmax*3.5f/12.5f);
		int height=12;
		for (int xi=1; xi<length;xi++){

		for(int yi=1; yi<ymax;yi++){dat[xi][yi][9]=3;dat[xmax-xi][yi][9]=3;}
			for (int yi=1; yi<6; yi++){dat[xi][yi][9]=1; dat[xi][ymax-yi][9]=1;dat[xmax-xi][yi][9]=1; dat[xmax-xi][ymax-yi][9]=1;}
			for (int yi=ymax/2-height; yi<ymax/2+height; yi++){
				dat[xi][yi][9]=1;
				dat[xmax-xi][yi][9]=1;
				}
			}
		// !Zhenyu
		*/
        // for (int i=5; i<100; i++){for(int j=5; j<20; j++){dat[i][j][9]=10;}}
        /*SolverThread*/
        {
            // Boundary Conditions
            // String BC= " 25 23 c 25 27 bs";
            //  TODO: https://git.rz.tu-bs.de/irmb/legacy-lbm-topology-optimization/-/issues/1
            String BC = null;
            if (BC != null) {
                StringTokenizer st = new StringTokenizer(BC);
                int n=st.countTokens()/3;
                int x1=0;
                int y1=0;
                for (int i=0; i<n; i++){
                    int xc=Integer.parseInt(st.nextToken());
                    int yc=Integer.parseInt(st.nextToken());
                    v[xc][yc][0]=0;
                    v[xc][yc][1]=0;
                    alpha[xc][yc]=0;
                    String k=st.nextToken();
                    //System.out.println(k);
                    if (k.compareTo("s")==0 | k.compareTo("S")==0) {dat[xc][yc][9]=1;}
                    else if (k.compareTo("v")==0 | k.compareTo("V")==0) {dat[xc][yc][9]=3;}
                    else if (k.compareTo("p")==0 | k.compareTo("P")==0) {dat[xc][yc][9]=2;}
                    else if (k.compareTo("c")==0 | k.compareTo("C")==0) {x1=xc;y1=yc;}
                    else if (k.compareTo("bs")==0 | k.compareTo("BS")==0) {
                        for (int xi=x1; xi<=xc; xi++){
                            for (int yi=y1; yi<=yc; yi++){dat[xi][yi][9]=1;}
                        }
                    }
                    else if (k.compareTo("bv")==0 | k.compareTo("BV")==0) {
                        for (int xi=x1; xi<=xc; xi++){
                            for (int yi=y1; yi<=yc; yi++){dat[xi][yi][9]=3;}
                        }
                    }
                }

            }
        }

        statusLabel = new JLabel("Status information");
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        statusLabel.setBorder(BorderFactory.createLoweredBevelBorder());

        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
        statusPanel.add(statusLabel);
        statusPanel.add(Box.createHorizontalGlue());

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));

        southPanel.add(control);
        southPanel.add(Box.createVerticalStrut(5)); // 添加间隔
        southPanel.add(statusPanel);

        // optControl.add(BorderLayout.EAST,optExpS);
        cp.add(BorderLayout.EAST,optControl);
        // cp.add(BorderLayout.SOUTH,southPanel);
        cp.add(BorderLayout.CENTER,display);
        cp.add(southPanel, BorderLayout.SOUTH);
    }

    // private void setUpWindowListeners() {
    //     setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    // }

    public class Display extends JPanel implements MouseListener, MouseMotionListener {
        public Display(){this.addMouseListener(this);
            this.addMouseMotionListener(this);
        }
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            if (clock.isSelected()){
                g.drawString("Time (s): "+Float.toString((System.currentTimeMillis()-start)/1000.f),10,(int)(sc*ymax+15));
                g.drawString("Timestep: "+Integer.toString(t-time),150,(int)(sc*ymax+15));
            }
            else {
                g.setColor(Color.blue);
                g.drawString("Time (s): "+Float.toString((start)/1000.f),10,(int)(sc*ymax+15));
                g.drawString("Timestep: "+Integer.toString(time),150,(int)(sc*ymax+15));
            }
            boolean ct=colorToc.isSelected();

            g.setColor(Color.blue);
            //g.drawLine(1,1,20,20);
            for (int xi=xmax-1;xi>0;xi--){
                //
                //g.drawLine((int)((xi)*sc),(int)((ymax)*sc+dat[xi][0][0]/(outflow/15)),(int)((xi-1)*sc),(int)((ymax)*sc+dat[xi-1][0][0]/(outflow/15)));
                //
                for (int yi=0;yi<ymax;yi++){
                    //	System.out.println(Float.toString(v[xi][yi][0]));
                    float c=(1-alpha[xi][yi]);
                    //g.setColor(Color.getHSBColor((float)(alpha[xi][yi]/2-0.5),0.95f,1f));
                    if(c<0.95 && dat[xi][yi][9]==0){
                        g.setColor(new Color(c,c,c));
                        g.fillRect((int)((xi-0.5)*sc),(int)((yi-0.5)*sc),(int)sc,(int)sc);
                    }
                    else{
                        g.setColor(Color.blue);
                        switch (dat[xi][yi][9]){
                            //case 10 :
                            case 3:
                                g.setColor(Color.yellow);
                                g.fillRect((int)((xi-0.5)*sc),(int)((yi-0.5)*sc),(int)sc,(int)sc);
                                g.setColor(Color.blue);


                            case 0:
                                //if (dat[xi][yi][9]==0){
                                //if(  (xi%3==0) & (yi%3==0)) {
                                //-- Hightest
                                //float den=0.1f*(dat[xi][yi][0]-(12.0f)*outflow*0.8f)/outflow;
                                if (ct){
                                    float dep=((float)Math.sqrt(v[xi][yi][0]*v[xi][yi][0]+v[xi][yi][1]*v[xi][yi][1]));
                                    g.setColor(Color.getHSBColor((float)(0.5f+dep*10),0.95f,1f));}
                                //System.out.println(Float.toString(dep));
                                //--!Hightest
                                g.drawLine((int)((xi)*sc),(int)((yi)*sc),(int)(((xi)+v[xi][yi][0]*vsc)*sc),(int)(((yi)-v[xi][yi][1]*vsc)*sc));
                                break;
                            //}
                            //}
                            //else{
                            case 1:
                                g.setColor(Color.green);
                                g.fillRect((int)((xi-0.5)*sc),(int)((yi-0.5)*sc),(int)sc,(int)sc);
                                g.setColor(Color.blue);
                                break;
                            case 2:
                                g.setColor(Color.red);
                                g.fillRect((int)((xi-0.5)*sc),(int)((yi-0.5)*sc),(int)sc,(int)sc);
                                g.setColor(Color.black);
                                //g.drawLine((int)(xi*sc),(int)(yi*sc),(int)((xi+v[xi][yi][0]*vsc)*sc),(int)((yi-v[xi][yi][1]*vsc)*sc));
                                g.drawString("("+Float.toString(v[xi][yi][0])+","+Float.toString(v[xi][yi][1])+","+Float.toString(alpha[xi][yi])+")",(int)(xi*sc),(int)(yi*sc));
                                g.setColor(Color.blue);
                                break;
                        }
                    }
                }

            }
            //g.drawString(Float.toString(om), 40, 80);
        }
        int xa=0,
                ya=0;
        //bool pressed=false
        public void mouseMoved (MouseEvent e){
            int x=(int)(e.getX()/sc+0.5);
            int y=(int)(e.getY()/sc+0.5);
            showStatus("("+Integer.toString(x)+","+Integer.toString(y)+")");
        }
        public void mouseEntered (MouseEvent e){}
        public void mouseClicked (MouseEvent e){}
        public void mouseReleased (MouseEvent e){}
        public void mouseDragged (MouseEvent e){
            //showStatus("MouseDraggeed");
            int x=(int)(e.getX()/sc+0.5);
            int y=(int)(e.getY()/sc+0.5);
            if (x<xmax & y<ymax) {
                int dx=x-xa;
                int dy=y-ya;
                int o,u,o1,u1;
                if (Math.abs(dx)>Math.abs(dy)){
                    if (xa<x){u=xa;
                        u1=ya;
                        o=x;
                        o1=y;}
                    else {o=xa;
                        o1=ya;
                        u=x;
                        u1=y;}
                    for (int i=u;i<o;i++){
                        dat[i][u1+(o1-u1)*(i-u)/(o-u)][9]=penColor;
                        alpha[i][u1+(o1-u1)*(i-u)/(o-u)]=0.9f;
                        v[i][u1+(o1-u1)*(i-u)/(o-u)][0]=0;
                        v[i][u1+(o1-u1)*(i-u)/(o-u)][1]=0;
                    }
                }
                else{
                    if (ya<y){u=ya;
                        u1=xa;
                        o=y;
                        o1=x;}
                    else {o=ya;
                        o1=xa;
                        u=y;
                        u1=x;}
                    for (int i=u;i<o;i++){
                        dat[u1+(o1-u1)*(i-u)/(o-u)][i][9]=penColor;
                        alpha[u1+(o1-u1)*(i-u)/(o-u)][i]=0.9f;
                        v[u1+(o1-u1)*(i-u)/(o-u)][i][0]=0;
                        v[u1+(o1-u1)*(i-u)/(o-u)][i][1]=0;
                    }

                }
                dat[x][y][9]=penColor;
                alpha[xa][ya]=0;}
            xa=x;
            ya=y;
            repaint();
        }

        public void mousePressed (MouseEvent e){
            //showStatus("MausPressesd");
            xa=(int)(e.getX()/sc+0.5);
            ya=(int)(e.getY()/sc+0.5);
            if (xa<xmax & ya<ymax) {dat[xa][ya][9]=penColor;
                alpha[xa][ya]=0.9f;
                v[xa][ya][0]=0;
                v[xa][ya][1]=0;
            }
            repaint();
        }
        public void mouseExited (MouseEvent e){
//            showStatus("");
        }

    };

    public class SolverThread extends Thread{
        SolverThread(){start();}
        public void run(){
//            while(true){
//                update(sz);
//                display.repaint();
//            }
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    // Perform task
                    update(sz);
                    display.repaint();
                    // Check for interruption
                    if (Thread.interrupted()) {
                        // Clear interrupted status and handle interruption
                        throw new InterruptedException();
                    }
                }
            } catch (InterruptedException e) {
                // Thread was interrupted, perform cleanup
            }
        }
        public void update(int times){
            for (int i=0; i<times; i++){
                for (int yi=0;yi<ymax;yi++){

//                    showStatus(Integer.toString(inflow));
                    int ei=-t+((xmax-1+t)/xmax)*xmax;
                    int wi=+t-((t)/xmax)*xmax;
                    int ni=yi+t-((yi+t)/ymax)*ymax;
                    int si=yi-t+((ymax-1-yi+t)/ymax)*ymax;
                    //System.out.println(Integer.toString(wi));
                    final float ov=0.95f;
                    //final float ow=0.999f;
                    float vvx=(vx*(1-ov*(vp))+v[xmax-1][yi][0]*ov*(vp));//(v[xmax][yi-1][0]+v[xmax][yi][0]+v[xmax][yi+1][0])/3*(1.0f-ov)+ov*vx;
                    float vy=     vp*ov*(dat[ei][ni][7]-dat[ei][si][5])/((float)(dat[ei][ni][7]+dat[ei][si][5]+dat[ei][yi][6]));//(v[xmax-1][yi][1]*ov);//(v[xmax][yi-1][1]+v[xmax][yi][1]+v[xmax][yi+1][1])/3*(1.0f-ov);
                    float vy2=(1-vp)*ov*(dat[wi][ni][7]-dat[wi][si][5])/((float)(dat[wi][ni][7]+dat[wi][si][5]+dat[wi][yi][6]));
                    //float rh=outflow*(1.0f-ow)+ow*(dat[xmax-1][yi][0]*(9.0f/4.0f/36.0f)/(1.0f-(3.0f/2.0f)*(vvx*vvx+vy*vy)));
                    //rh=outflow;
                    dat[wi][ni][1]=(int)(outflow*(1+3*(-vvx+vy+vvx*vvx+vy*vy)));
                    dat[wi][si][3]=(int)(outflow*(1+3*(-vvx-vy+vvx*vvx+vy*vy)));
                    dat[wi][yi][2]=(int)(4*outflow*(1+3*(-vvx+vvx*vvx)));
                    vvx=(vx*(1-ov*(1-vp))+v[1][yi][0]*ov*(1-vp));//(v[1][yi-1][0]+v[1][yi][0]+v[1][yi+1][0])/3*(1.0f-ov)+ov*vx;
                    //vy=(v[1][yi][1]*ov*(1-vp));//(v[1][yi-1][1]+v[1][yi][1]+v[1][yi+1][1])/3*(1.0f-ov);
                    //rh=outflow*(1.0f-ow)+ow*(dat[1][yi][0]*(9.0f/4.0f/36.0f)/(1.0f-(3.0f/2.0f)*(vvx*vvx+vy2*vy2)));
                    dat[ei][si][5]=(int)(outflow*(1+3*(vvx-vy2+vvx*vvx+vy2*vy2)));
                    dat[ei][ni][7]=(int)(outflow*(1+3*(vvx+vy2+vvx*vvx+vy2*vy2)));//inflow;
                    dat[ei][yi][6]=(int)(4*outflow*(1+3*(vvx+vvx*vvx)));//4*inflow;
                    //dat[wi][10][9]=1;
                }

                float rho,vx,vy;
                //double q=0;
                //for (int chess=0; chess<2;chess++)
                //for (int ch=0; ch<2;ch++)
                {
                    {
                        for (int xi=1/*+cess*/;xi<xmax;xi++){
                            for (int yi=0/*+((chess+ch==1) ? 1 :0)*/;yi<ymax;yi++){
                                //
                                //for (int ii=0; ii<10000; ii++){{
                                //int xi=(int)(Math.random()*(xmax-1));
                                //int yi=(int)(Math.random()*(ymax-1));
                                //
                                int ei=xi-t+((xmax-1-xi+t)/xmax)*xmax;
                                int wi=xi+t-((xi+t)/xmax)*xmax;
                                int ni=yi+t-((yi+t)/ymax)*ymax;
                                int si=yi-t+((ymax-1-yi+t)/ymax)*ymax;
                                //dat[ei][0][9]=1;
                                int nw=dat[wi][ni][1];
                                int w=dat[wi][yi][2];
                                int sw=dat[wi][si][3];
                                int s=dat[xi][si][4];
                                int se=dat[ei][si][5];
                                int e=dat[ei][yi][6];
                                int ne=dat[ei][ni][7];
                                int n=dat[xi][ni][8];
                                int r=dat[xi][yi][0];
                                switch( dat[xi][yi][9]){
                                    //if (dat[xi][yi][9]==1){
                                    case 1:
                                        dat[wi][ni][1]=se;
                                        dat[wi][yi][2]=e;
                                        dat[wi][si][3]=ne;
                                        dat[xi][si][4]=n;
                                        dat[ei][si][5]=nw;
                                        dat[ei][yi][6]=w;
                                        dat[ei][ni][7]=sw;
                                        dat[xi][ni][8]=s;
                                        break;
                                    //}
                                    //else{
                                    case 10:
                                    default:

                                        rho=nw+w+sw+s+se+e+ne+n+r+1.0e-10f;
                                        float al=alpha[xi][yi];
                                        al=al*al*al;
                                        int px=(int)(al*optExp*(ne+e+se-nw-w-sw)/6);
                                        int py=(int)(al*optExp*(ne+n+nw-se-s-sw)/6);
                                        float om_n=0.5f*al+(1-al)*om;
                                        vx=(ne+e+se-nw-w-sw)/rho;
                                        vy=(ne+n+nw-se-s-sw)/rho;
                                        vx= (vx> 0.4f ) ? 0.4f : ((vx< -0.4f) ? -0.4f : vx);
                                        vy= (vy> 0.4f ) ? 0.4f : ((vy< -0.4f) ? -0.4f : vy);
                                        v[xi][yi][0]=vx;
                                        v[xi][yi][1]=vy;

                                        //ob[xi][yi][0]=(1-obom)*ob[xi][yi][0]+obom*vx;
                                        //ob[xi][yi][1]=(1-obom)*ob[xi][yi][1]+obom*vy;
                                        nw+=px-py;
                                        w+=px;
                                        sw+=px+py;
                                        s+=py;
                                        n-=py;
                                        se+=-px+py;
                                        e+=-px;
                                        ne+=-px-py;
                                        //double pp=(alpha[xi][yi]*alpha[xi][yi]*(py*py+px*px)/(4*rho*rho));
                                        //alpha[xi][yi]+=(0.001f-pp)*0.00001f;
                                        //alpha[xi][yi]= (alpha[xi][yi]<1e-4f) ? 1e-4f : ((alpha[xi][yi]>0.9999f) ? 0.9999f : alpha[xi][yi]);

                                        // double pp;//=vx*vx+vy*vy;
                                        //double ppx=(vx*(alpha[((xi>1) ? xi-1 : xmax-1)][(yi+1)%ymax]+alpha[xi][(yi+1)%ymax]+alpha[(xi+1)%xmax][(yi+1)%ymax]-alpha[((xi>1) ? xi-1 : xmax-1)][((yi>1) ? yi-1 : ymax-1)]-alpha[xi][((yi>1) ? yi-1 : ymax-1)]-alpha[(xi+1)%xmax][((yi>1) ? yi-1 : ymax-1)]));
                                        //double ppy=vy*(alpha[(xi+1)%xmax][((yi>1) ? yi-1 : ymax-1)]+alpha[(xi+1)%xmax][(yi+1)%ymax]-alpha[((xi>1) ? xi-1 : xmax-1)][((yi>1) ? yi-1 : ymax-1)]-alpha[((xi>1) ? xi-1 : xmax-1)][yi]-alpha[((xi>1) ? xi-1 : xmax-1)][(yi+1)%ymax]);
                                        //double pp=ppx*ppx+ppy*ppy;
                                        //	pp=1+pp;
                                        //pp=pp*pp;
                                        //pp=pp*pp;
                                        //pp=pp*pp;
                                        //	pp=Math.pow(pp,optPow);
                                        //	pp=pp*pp-1;

                                        //optOm=0.5f;
                                        vx=(ne+e+se-nw-w-sw)/rho;
                                        vy=(ne+n+nw-se-s-sw)/rho;

                                        //al=(float)(al+optA*(3*(vx*vx+vy*vy)-2*(vvx*vx+vvy*vy)));

                                        //--- inverse
                                        //double sal=alpha[(xi+1)%xmax][yi]+alpha[((xi>1) ? xi-1 : xmax-1)][yi]+alpha[xi][(yi+1)%ymax]+alpha[xi][((yi>1) ? yi-1 : ymax-1)];
                                        //if (sal>0.5f)
                                        //original!
					/*{al=(float)((1-optOm)*al+optOm*(1-pp*optA));}

					q+=(alpha[xi][yi]= (al<1e-9f) ? 1e-9f : ((al>optM) ? optM : al))/optM;*/
                                        //default:
                                        //rho=nw+w+sw+s+se+e+ne+n+r+1.0e-10f;


                                        //float o= (Math.abs(vx)<0.3 | Math.abs(vy)<0.3)? om:1;
                                        //if (o==1){System.out.println("om1");}

                                        //int NP=(int)(((n+s+e+w)*(1./6.)+1./12.*(ne+nw+se+sw-rho)));
                                        int P=(int)(1./12.*(rho*(vx*vx+vy*vy)-e-n-s-w-2*(se+sw+ne+nw-1./3.*rho)));
                                        int NE=(int)(om_n*0.25*(n+s-e-w+rho*(vx*vx-vy*vy)));
                                        int V=(int)(om_n*((ne+sw-nw-se)-vx*vy*rho)*0.25);
                                        int UP=(int)(-(.25*(se+sw-ne-nw-2.*vx*vx*vy*rho+vy*(rho-n-s-r))-vy*.5*(-3.*P-NE)+vx*((ne-nw-se+sw)*.5-2*V)));
                                        int RIGHT=(int)(-(.25*(sw+nw-se-ne-2.*vy*vy*vx*rho+vx*(rho-r-w-e))-vx*.5*(-3.*P+NE)+vy*((ne+sw-se-nw)*.5-2*V)));
                                        int NP=(int)(0.25f*(
                                                rho*(1.f/9.f)-ne-nw-se-sw-8*P
                                                        +2*(vx*(ne-nw+se-sw-4*RIGHT)+vy*(ne+nw-se-sw-4*UP))
                                                        +4*vx*vy*(-ne+nw+se-sw+4*V)
                                                        +vx*vx*(-n-ne-nw-s-se-sw+2*NE-6*P)
                                                        +vy*vy*((-e-ne-nw-se-sw-w-2*NE-6*P)+3*vx*vx*rho)
                                        ));
                                        dat[wi][ni][1]=nw+2*P+NP+V-UP+RIGHT;
                                        dat[wi][yi][2]=w-P-2*NP+NE-2*RIGHT;
                                        dat[wi][si][3]=sw+2*P+NP-V+UP+RIGHT;
                                        dat[xi][si][4]=s-P-2*NP-NE-2*UP;
                                        dat[ei][si][5]=se+2*P+NP+V+UP-RIGHT;
                                        dat[ei][yi][6]=e-P-2*NP+NE+2*RIGHT;
                                        dat[ei][ni][7]=ne+2*P+NP-V-UP-RIGHT;
                                        dat[xi][ni][8]=n-P-2*NP-NE+2*UP;
                                        dat[xi][yi][0]=r+(4*(-P+NP));
                                        //-----v change
                                        //	double dx=(vx-v[xi][yi][0])/(vx+1.0e-9);
                                        //	double dy=(vy-v[xi][yi][1])/(vy+1.0e-9);

                                        //-----
					/*v[xi][yi][0]=vx;
					v[xi][yi][1]=vy;*/

                                        //Stresstest
                                        //original!
                                        //System.out.println(Float.toString(-(float)((dx*dx+dy*dy)*optPow*0.0)));
                                        //double pp_=vx*vx+vy*vy;
                                        //	pp=vx*vx+vy*vy-((dx*dx+dy*dy)*optPow/1000);
                                        //if (pp!=pp_){
                                        //System.out.println(Double.toString(pp-pp_));}
                                        //!!pp=(vx*vx+vy*vy)-(double)P/rho*optPow;//-((double)V*(double)V+(double)NE*(double)NE)/(rho*rho)*optPow;
                                        //!!{al=(float)((1-optOm)*al+optOm*(1-pp*optA));}
                                        //System.out.println(Double.toString((double)pp));
                                        //System.out.println(Double.toString((V*V+NE*NE)));
                                        //!!q+=(alpha[xi][yi]= (al<1e-9f) ? 1e-9f : ((al>optM) ? optM : al))/optM;
                                        //
                                }
                            }
                        }
                    }
                }
                //if(t%10==0)
                {//double aa;
                    //do{//testloop


                    {
                        //	mass(q);
                        //	q=updateGeo(updateGradient(),0.9f*q+0.1f*optS);
                        updateGradient();
                        inversePore(optSc,eq());
                    }

                    //aa=optA;
                    //optA=(int)(optA*(1+/*optOm*/2*(q-optS)/(xmax*ymax)));//abhaengigkeit geaendert 21.7.05
                    //optA=(optA<1) ? 1 : optA;
                    //	optA=optA+(optS-q)/(xmax*ymax);
                    //optA=0.1*optA+0.9*aa;
                    //System.out.println(Float.toString((float)optA));
                    //aa=(optA-aa)/(optA+aa);
                    //aa*=aa;
                    //}while(aa>1.0e-15);//testloop
                }
                //mass(q);
                t++;
            }
        }

        public double eq (){
            double sum=1e-100;
            int remove=0;
            for (int xi=1; xi<xmax;xi++){
                for (int yi=0; yi<ymax; yi++){
                    if (alpha[xi][yi]>0.1f) sum+=dalpha[xi][yi];//(v[xi][yi][0]*v[xi][yi][0]+v[xi][yi][1]*v[xi][yi][1]);//dalpha[xi][yi];
                    else remove++;
                }
            }
            double ret =(xmax*ymax-optS-remove)/(sum);
            return (ret<0) ? 0 : ret;
        }

        public void inversePore(double om, double eq){
            //System.out.println(Double.toString(om));
            for (int xi=1; xi<xmax;xi++){
                for (int yi=0; yi<ymax; yi++){
                    double a=(float)(1-((1-alpha[xi][yi])*(1-om)+om*(eq*dalpha[xi][yi])));//(alpha[xi][yi]/((1.-om)+eq*alpha[xi][yi]*om*(dalpha[xi][yi])+1.0e-10));
                    //System.out.println(Double.toString(a));
                    //System.out.println(Double.toString(eq));
                    alpha[xi][yi]= (a>0.99999f) ? 0.999999f : ((a<1e-10f) ? 1e-10f : (float)a);
                }
            }
        }
        /*	public void mass (double q1){
                if (optS>0){
                if (q1>optS){
                    float sc=(float)((q1*0.9f+0.1f*optS)/q1);
                    //System.out.println(Float.toString(sc));
                    for (int xi=1; xi<xmax;xi++){
                        for (int yi=0; yi<ymax; yi++){
                            alpha[xi][yi]*=sc;//sc;
                            }
                        }
                    }
                    else {
                        float sc=(float)((xmax*ymax-(q1*0.9f+0.1f*optS))/(xmax*ymax-q1));
                        for (int xi=1; xi<xmax;xi++){
                        for (int yi=0; yi<ymax; yi++){
                            alpha[xi][yi]=1-sc*(1-alpha[xi][yi]);//sc;
                            }
                        }
                        }
                        }
                }*/
        public double updateGradient(){
            double app=0;
            for (int xi=1; xi<xmax;xi++){
                for (int yi=0; yi<ymax; yi++){
                    int wi=xi-1+((xmax-xi)/xmax)*xmax;
                    int ei=xi+1-((xi+1)/xmax)*xmax;
                    int si=yi+1-((yi+1)/ymax)*ymax;
                    int ni=yi-1+((ymax-yi)/ymax)*ymax;
                    float vx=v[xi][yi][0];
                    float vy=v[xi][yi][1];
                    float vx1=v[wi][yi][0]+v[ei][yi][0]+v[xi][si][0]+v[xi][ni][0];
                    float vx2=v[wi][ni][0]+v[wi][si][0]+v[ei][ni][0]+v[ei][si][0];
                    float vy1=v[wi][yi][1]+v[ei][yi][1]+v[xi][si][1]+v[xi][ni][1];
                    float vy2=v[wi][ni][1]+v[wi][si][1]+v[ei][ni][1]+v[ei][si][1];
                    vx=(vx*16+vx1*4+vx2)/36;
                    vy=(vy*16+vy1*4+vy2)/36;
                    //vx=(vy*vy+vx*vx);
                    //vx=vx*vx*vx;
                    //	float al=(alpha[xi][yi]*16+(alpha[wi][yi]+alpha[ei][yi]+alpha[xi][ni]+alpha[xi][si])*4+alpha[ei][ni]+alpha[ei][si]+alpha[wi][ni]+alpha[wi][si])/36-1;
                    //final float b=0.8f;
                    app+=dalpha[xi][yi]=dalpha[xi][yi]*(obom) +(1-obom)*(vy*vy+vx*vx)+1.0e-20f;//*/*(1.000001f-(float)Math.exp(-al*50));*/((float)Math.exp(-al*al*optExp));//(1-0.9f*alpha[xi][yi]);
                    //dalpha[xi][ni]-=(vx*vx)*((float)Math.exp(-al*al/2007.42));
                    //dalpha[xi][si]-=(vx*vx)*((float)Math.exp(-al*al/2007.42));
                    //dalpha[ei][yi]-=(vy*vy)*((float)Math.exp(-al*al/2007.42));
                    //dalpha[wi][yi]-=(vy*vy)*((float)Math.exp(-al*al/2007.42));
                }}
            //return 1;
            return (app+1e-16)/(xmax*ymax);
        }
        /*public void orthoGrad(){
            for (int yi=1;yi<ymax;yi+=2){
                for (int xi=2; xi<xmax;xi+=2){
                    float a1,a2,a3,a4;
                    a1=dalpha[xi-1][yi-1];
                    a2=dalpha[xi][yi-1];
                    a3=dalpha[xi-1][yi];
                    a4=dalpha[xi][yi];
                    dalpha[xi-1][yi-1]+=a4*0.1f;
                    dalpha[xi][yi-1]+=-a3*0.1f;
                    dalpha[xi-1][yi]+=a2*0.1f;
                    dalpha[xi][yi]+=-a1*0.1f;
                }}
            }*/
		/*public void redGrad(){
			for (int yi=1;yi<ymax;yi+=2){
				for (int xi=2; xi<xmax;xi+=2){
					float a;
					a=(dalpha[xi-1][yi-1]+dalpha[xi][yi-1]+dalpha[xi-1][yi]+dalpha[xi][yi])/4;
					dalpha[xi-1][yi-1]-=a;
					dalpha[xi][yi-1]-=a;
					dalpha[xi-1][yi]-=a;
					dalpha[xi][yi]-=a;
				}}
			}*/
        public double updateGeo(double app_o,double qa){
            //double app;
            double q = 0;
            double dq = 0;
            //--logistic controll
		/*
		{
		//double minC=0;
		double  slopC=0;
		double  slopD=0;
		float pmin=0;
		for (int xi=1; xi<xmax;xi++){
				for (int yi=0; yi<ymax; yi++){
				float pp=(float)(-dalpha[xi][yi]);
				float al=alpha[xi][yi];
				if (al>1.0e-9f){
					//minC-=al;
					slopC+=pp;
					}
					pmin=(pmin<pp)? pmin :pp;
				}}
				//if (-slopC/app_o>qa){
				app=(qa+1e-10f)/(-slopC+1e-10f)*2;
				//}
				//else app=app_o;
				slopC/=app_o;
			for (int xi=1; xi<xmax;xi++){
				for (int yi=0; yi<ymax; yi++){
				if (alpha[xi][yi]<0.9999f){slopD+=-dalpha[xi][yi]/app-pmin;}
				}}
			double maxC=(xmax-1)*ymax-qa;
			double bb=((-qa-maxC)/(slopC-maxC+1e-20)-1);//-(1-(-qa-maxC)/(slopC-maxC+1.0e-20));
			System.out.println(Float.toString((float)app));
			System.out.println(Float.toString((float)app_o));
			optA=-pmin*(Math.log((-1-(-qa-maxC)/(maxC-(-1.0e-8+0*1.0e-8*(optS-qa))))/bb)/(Math.log((-1-(-qa-maxC)/(maxC-slopD))/bb)));
		optA=0;
		}
		optA=(optA==optA && optA!=Double.POSITIVE_INFINITY && optA!=Double.NEGATIVE_INFINITY)? optA: 0;*/
            //--!logistic controll
            //--growth approx
            //float tet,tets;
            //int itr=0;
            //do
            {
                //optA=optA+(optS-qa)/(xmax*ymax);
                double maxC=0;
                double  slopC=0;
                //double maxM=0;
                //double slopM=0;
                //double maxD=0;
                //double slopD=0;
                for (int xi=1; xi<xmax;xi++){
                    for (int yi=0; yi<ymax; yi++){
                        float pp=(float)(/*optA*/-dalpha[xi][yi]/app_o);
                        float al=alpha[xi][yi];
                        //slopD-=pp;
                        if (pp>0){
                            if (al<0.99999f){
                                //slopM+=pp;
                                slopC+=pp;
                                //maxC+=1-al;
                                //maxM+=1-al;
                            }
                            //maxD+=al;
                        }
                        else{
                            if (al>1.0e-8f){
                                slopC-=pp;
                                //slopM+=pp;
                                //maxM-=al;
                                maxC+=al;
                            }
                            //maxD-=1-al;
                        }
                    }}

                optOm=(float)(-/*Math.log(0.95)*/optSc*(maxC/(slopC+1.0e-10f)));
                //--restriction
                //float tet=(float)(maxC*(1-Math.exp(-optOm*slopC/maxC)));
                //final float tets=0.004f;
                //if (tet>tets*xmax*ymax){System.out.println(Float.toString(tet));optOm=(float)(-(maxC/(slopC+1.0e-10f))*Math.log(1-tets*xmax*ymax/maxC));}
                //tet=(float)(maxM*(1-Math.exp(-optOm*slopM/maxM)));
                //tets=(float)(optS-qa);
                //System.out.println(Float.toString((float)optA));
                //System.out.println(Float.toString((float)optOm));
                //System.out.println(Float.toString(tet));
                //System.out.println(Float.toString(tets));
                //System.out.println(Float.toString((float)(-(maxM/(slopM+1.0e-10f))*Math.log(1-tets/maxM))));
                //System.out.println(Float.toString((float)(-(maxD/(slopD+1.0e-10f))*Math.log(1-tets/maxD))));
                //if (tet>0 && tet>tets | tet<0 && tet<tets){System.out.println(Float.toString(tet));orthoGrad();/*optOm=(float)(-(maxM/(slopM+1.0e-10f))*Math.log(1-tets/maxM));*/}
                //if(tet*tets<0){System.out.println("stopped");redGrad();/*optOm=(float)(-(maxD/(slopD+1.0e-10f))*Math.log(1-tets/maxD))*/;}
                //if(((tet>0 && tets>0 && tet*2>tets) |(tet<0 && tets<0 && tet*2<tets))){optOm=(float)(-(maxM/(slopM+1.0e-10f))*Math.log(1-tets/maxM/2));}
                //	if (tet>20) {optOm=(float)(-(maxM/(slopM+1.0e-10f))*Math.log(1-20/maxM));}
                //	else{if (tet<20) {optOm=(float)(-(maxM/(slopM+1.0e-10f))*Math.log(1+20/maxM));}}
                //itr++;
                //if (itr>50){optOm=0;break;}
                //optOm=(float)(-(maxM/(slopM+1.0e-10f))*Math.log(1+optS*optSc/maxM/2));
            }//while(false);//(tet*tets<0);
            optOm=(optOm==optOm)? optOm:0;
            //--!growth approx
            //---reg mass
		/*double app=0;
		for (int xi=1; xi<xmax;xi++){
				for (int yi=0; yi<ymax; yi++){
				int wi=xi-1+((xmax-xi)/xmax)*xmax;
				int ei=xi+1-((xi+1)/xmax)*xmax;
				int si=yi+1-((yi+1)/ymax)*ymax;
				int ni=yi-1+((ymax-yi)/ymax)*ymax;
				float vx=ob[xi][yi][0];
				float vy=ob[xi][yi][1];
				float vx1=ob[wi][yi][0]+ob[ei][yi][0]+ob[xi][si][0]+ob[xi][ni][0];
				float vx2=ob[wi][ni][0]+ob[wi][si][0]+ob[ei][ni][0]+ob[ei][si][0];
				float vy1=ob[wi][yi][1]+ob[ei][yi][1]+ob[xi][si][1]+ob[xi][ni][1];
				float vy2=ob[wi][ni][1]+ob[wi][si][1]+ob[ei][ni][1]+ob[ei][si][1];
				vx=(vx*16+vx1*4+vx2)/36;
				vy=(vy*16+vy1*4+vy2)/36;
					app+=(vx*vx+vy*vy)+1e-9;
					}}
					app/=xmax*ymax;
		//---!reg mass
		*/
            float al;
            for (int xi=1; xi<xmax;xi++){
                for (int yi=0; yi<ymax; yi++){
                    if (dat[xi][yi][9]!=3){
				/*int wi=xi-1+((xmax-xi)/xmax)*xmax;
				int ei=xi+1-((xi+1)/xmax)*xmax;
				int si=yi+1-((yi+1)/ymax)*ymax;
				int ni=yi-1+((ymax-yi)/ymax)*ymax;
				float vx=ob[xi][yi][0];
				float vy=ob[xi][yi][1];
				float vx1=ob[wi][yi][0]+ob[ei][yi][0]+ob[xi][si][0]+ob[xi][ni][0];
				float vx2=ob[wi][ni][0]+ob[wi][si][0]+ob[ei][ni][0]+ob[ei][si][0];
				float vy1=ob[wi][yi][1]+ob[ei][yi][1]+ob[xi][si][1]+ob[xi][ni][1];
				float vy2=ob[wi][ni][1]+ob[wi][si][1]+ob[ei][ni][1]+ob[ei][si][1];
				vx=(vx*16+vx1*4+vx2)/36;
				vy=(vy*16+vy1*4+vy2)/36;
					float pp=(vx*vx+vy*vy);*/ //*(alpha[xi][yi]*0.5f+5.0e-1f);//-((double)V*(double)V+(double)NE*(double)NE)/(rho*rho)*optPow;
                        al=(float)(alpha[xi][yi]+optOm*(/*optA*/-/*Math.pow(pp,optExp)*/dalpha[xi][yi]/app_o/*optA*/));
                        q+=al=(al= (al<1e-9f) ? 1e-9f : ((al>1) ? 1 : al));
                    }
                    else{al=0;}
                    //dq+=(al-alpha[xi][yi])*(al-alpha[xi][yi]);

                    alpha[xi][yi]=al;
                }
            }
            {
                //dq=(dq==0)? optSc : optSc/((float)(dq/(xmax*ymax)));
				/*double qdif=(optOm-dq);
				double qsum=(optOm+dq+1.0e-9f);

				qdif*=qdif;
				qsum*=qsum;
				double mi,mx;
				if (dq>optOm){mi=optOm;mx=dq;}
				else {mi=dq;mx=optOm;}
				optOm=(float)(qdif/qsum*mi+(qsum-qdif)/qsum*mx);*/
                //optOm=(float)((optOm+1.0e-9f)*dq);
                //optOm=optOm*0.9f+0.1f*0.001f/((float)(dq/(xmax*ymax))+1.0e-9f);
            }
            //System.out.println(Float.toString((float)(q-qa)));
            //System.out.println("");

            return q;
        }
    };

    public void showStatus(String message) {
        statusLabel.setText(message);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                TopologyApp app = new TopologyApp();
                app.pack();
                app.setVisible(true);
            }
        });
    }
}
