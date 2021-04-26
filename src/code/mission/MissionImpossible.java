package code.mission;

import code.generic.SearchProblem;
import code.generic.SearchTreeNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

public class MissionImpossible extends SearchProblem {

    byte m;
    byte n;
    byte submarineX;
    byte submarineY;
    String IMFLocations;
    String IMFHealth;
    byte truckCapacity;
    ArrayList<SearchTreeNode> path = new ArrayList<>();

    MissionImpossible(byte[] operators, String initialState, HashSet<String> visitedStates, int numNodesExpanded, byte m, byte n, byte submarineX, byte submarineY, String IMFLocations, String IMFHealth, byte truckCapacity ) {
        super(operators, initialState, visitedStates, numNodesExpanded);
        this.m =m;
        this.n=n;
        this.submarineX= submarineX;
        this.submarineY=submarineY;
        this.IMFLocations=IMFLocations;
        this.IMFHealth=IMFHealth;
        this.truckCapacity=truckCapacity;

    }


    //generates a random grid
    public static String GenGrid() {
        //grid
        String grid="";
        Random random = new Random();
        //generate dimensions of mxn grid
        int m  = random.nextInt(11) + 5;
        int n = random.nextInt(11) + 5;
        grid = grid.concat(m+","+n+";");

        //Ethan initial position
        int ethanPosX= random.nextInt(m);
        int ethanPosY= random.nextInt(n);
        grid = grid.concat(ethanPosX+","+ethanPosY+";");

        //Submarine position
        int submarinePosX= random.nextInt(m);
        int submarinePosY= random.nextInt(n);

        //Initially, all cells are either empty or contain one of the following: Ethan, submarine, IMF member
        //check that submarine isnt in the same place as Ethan before concatenation
        while(submarinePosX==ethanPosX && submarinePosY==ethanPosY)
        {
            //while submarine is in ethan's postion, generate new coordinates for the submarine
            submarinePosX= random.nextInt(m);
            submarinePosY= random.nextInt(n);
        }
        grid = grid.concat(submarinePosX+","+submarinePosY+";");
        //Number of IMF members
        int numMembers = random.nextInt(11) + 5;  //num between 5 and 15
        int[]imfX= new int[numMembers];
        int[]imfY= new int[numMembers];
        //locations of IMF members
        for(int i=0; i<numMembers;i++){
            int IMFPosX= random.nextInt(m);
            int IMFPosY= random.nextInt(n);
            //String[]coordinates = grid.split(",");
            imfX[i]=IMFPosX;
            imfY[i]=IMFPosY;
            while((IMFPosX==ethanPosX && IMFPosY==ethanPosY) ||(submarinePosX==IMFPosX && submarinePosY==IMFPosY) )
            {
                //while imf member is in ethan's or submarine's postion, generate new coordinates for the imf member
                IMFPosX= random.nextInt(m);
                IMFPosY= random.nextInt(n);

            }
            if(i>0){
                //check if two IMF are in same place + check if they are in submarine or ethans again
                for(int j=0; j<imfX.length;j++) {
                    while (IMFPosX == imfX[j] && IMFPosY == imfY[j] || (IMFPosX==ethanPosX && IMFPosY==ethanPosY) ||(submarinePosX==IMFPosX && submarinePosY==IMFPosY) ) {
                        IMFPosX= random.nextInt(m);
                        IMFPosY= random.nextInt(n);

                    }
                }
            }
            imfX[i]=IMFPosX;
            imfY[i]=IMFPosY;

            grid = grid.concat(IMFPosX+","+IMFPosY);
            if(i<numMembers-1)
                grid = grid.concat(",");
        }
        grid = grid.concat(";");
        grid = grid.replace(".","");

        //health of imf members
        for (int i=0; i<numMembers;i++){
            int IMFhealth = random.nextInt(99)+1;
            grid = grid.concat(IMFhealth+"");
            if(i<numMembers-1)
                grid = grid.concat(",");
        }
        grid = grid.concat(";");

        //generate truck capacity c
        int c = random.nextInt(numMembers)+1;
        grid = grid.concat(c+"");

        //System.out.println(grid);

        return grid;

    }

    public static String solve(String grid, String strategy, boolean visualize){

        String plan="";
        //parse grid
        String[] info= grid.split(";");
        byte m = (byte) Integer.parseInt(info[0].split(",")[0]);
        byte n = (byte) Integer.parseInt(info[0].split(",")[1]);

        byte ethanPosX = (byte) Integer.parseInt(info[1].split(",")[0]);
        byte ethanPosY = (byte) Integer.parseInt(info[1].split(",")[1]);

        byte submarinePosX= (byte) Integer.parseInt(info[2].split(",")[0]);
        byte submarinePosY= (byte) Integer.parseInt(info[2].split(",")[1]);

        //fourth cell contains positions of soliders
        String IMFCoordinates = info[3];
        //fifth cell contains health

        String IMFHealth= info[4];

        int numIMF = IMFHealth.split(",").length;
        String carryStatus ="";
        for (int i=0; i<numIMF;i++){
            carryStatus+="n";
        }
        byte truckCapacity =(byte) Integer.parseInt(info[5]);

        byte[] operators={0,1,2,3,4,5};

        String initialState = ethanPosX+","+ethanPosY+";"+carryStatus; //ethanPosX,ethanPosY;carryStatus
        HashSet<String> visitedStates = new HashSet<>();
        //create a mission impossible instance
        int numberOfExpandedNode=0;
        MissionImpossible mission = new MissionImpossible(operators, initialState, visitedStates, numberOfExpandedNode,m,n,submarinePosX, submarinePosY, IMFCoordinates, IMFHealth, truckCapacity);
        //pass it to search and get goal node
        SearchTreeNode solution = SearchProblem.GeneralSearchProcedure(mission,strategy);


        //pass node to get plan
        String planEncoded = mission.getPlan(solution);
        plan = mission.decodePlan(planEncoded); //plan;deaths;healths;nodes

        int deaths=0;
        int[]healthsEachStep = new int[numIMF];
        //boolean [] gotCarried= new boolean[numIMF];
        mission.getPathToGoal(solution);
        //System.out.println( "HH1: "+ mission.path.get(0).heuristic1);
        for(int i=0; i<mission.path.size();i++){
            SearchTreeNode stepOnPath = mission.path.get(i);
            int depth = stepOnPath.depth;
            String state= stepOnPath.state;
            String[] stepInfo = state.split(";");
            String stepCarryStatus = stepInfo[1];
            if(stepOnPath.operator== 4){
                //someone is carried
                //who?
                int healthFinal=0;
                byte ethanPosXCurr = (byte) Integer.parseInt(stepInfo[0].split(",")[0]);
                byte ethanPosYCurr = (byte) Integer.parseInt(stepInfo[0].split(",")[1]);
                String [] tempIMF = IMFCoordinates.split(",");
                for(int j =0; j<tempIMF.length-1;j++){
                    if(j%2==0){
                        byte IMFPosXCurr = (byte) Integer.parseInt(tempIMF[j]);
                        byte IMFPosYCurr = (byte) Integer.parseInt(tempIMF[j+1]);
                        if(IMFPosXCurr==ethanPosXCurr && IMFPosYCurr == ethanPosYCurr){
                            //this is carried
                            int healthInitial = Integer.parseInt(String.valueOf(mission.IMFHealth.split(",")[j/2]));
                             healthFinal = healthInitial + (depth*2) -2;
                             if(healthFinal>100)
                                 healthFinal=100;
                            healthsEachStep[j/2] = healthFinal;

                        }

                    }

                }

            }



        }
        String healthPoint="";
        for(int health: healthsEachStep){
            if(health>=100){
                deaths++;
            }
            healthPoint+=health+",";
        }
        healthPoint= healthPoint.substring(0, healthPoint.length() - 1)+";";
        plan+=deaths+";"+healthPoint+mission.numNodesExpanded;
        if(visualize){
            mission.visualiseGrid(mission.path);
            //System.out.println("NODES EXPANDED: "+ mission.numNodesExpanded);
        }
        return plan;

    }


    public String getPlan(SearchTreeNode node){
        if(node.parent==null)
            return "";
        return node.operator + getPlan(node.parent);
    }

    public void getPathToGoal(SearchTreeNode node){
        if(node.parent==null) {
            path.add(0,node);
            return;
        }
        path.add(0,node);
        getPathToGoal(node.parent);
        //return ;
    }

    public String decodePlan(String planEncoded){
        String decodedPlan="";

        for(int i=planEncoded.length()-1; i>-1;i--){
            int operator = Integer.parseInt(planEncoded.charAt(i)+"");
            switch (operator) {
                case 0 -> decodedPlan += "up,";
                case 1 -> decodedPlan += "down,";
                case 2 -> decodedPlan += "right,";
                case 3 -> decodedPlan += "left,";
                case 4 -> decodedPlan += "carry,";
                case 5 -> decodedPlan += "drop,";
            }
        }

        decodedPlan= decodedPlan.substring(0, decodedPlan.length() - 1)+";";


        return decodedPlan;
    }

    @Override
    public String StateSpace(String currentState, byte operator) {
        String nextState;
        String[] info = currentState.split(";");
        byte ethanPosX = (byte) Integer.parseInt(info[0].split(",")[0]);
        byte ethanPosY = (byte) Integer.parseInt(info[0].split(",")[1]);
        String carryStatus = info[1];
        char[] newCarryStatus=carryStatus.toCharArray();
        //up
        if(operator == 0){
            if(ethanPosX>0){
                ethanPosX--;
            }
        }
        //down
        if(operator==1){
            if(ethanPosX<m-1){
                ethanPosX++;
            }
        }
        //right
        if(operator==2){
            if(ethanPosY<n-1){
                ethanPosY++;
            }
        }
        //left
        if(operator==3){
            if(ethanPosY>0){
                ethanPosY--;
            }
        }
        //carry
        if(operator==4){
            byte currentCarried =0;
            //check if I can carry
            for(int i=0; i<carryStatus.length();i++){
                if(carryStatus.charAt(i)=='c'){
                    currentCarried++;
                }
            }
            //if I can..
            if(currentCarried<truckCapacity) {
                String[] coordinates = IMFLocations.split(",");
                //then is there someone to be carried?
                for (int i = 0; i < coordinates.length - 1; i++) {
                    if (i % 2 == 0) {
                        byte IMFX = (byte) Integer.parseInt(coordinates[i]);
                        byte IMFY = (byte) Integer.parseInt(coordinates[i + 1]);
                        if (ethanPosX == IMFX && ethanPosY == IMFY && carryStatus.charAt(i/2)=='n') {
                            newCarryStatus[(i/2)]='c';
                        }
                    }
                }
            }


        }
        //drop
        if(operator==5){
            //am I at the submarine?
            if(ethanPosX==submarineX && ethanPosY==submarineY){
                //drop all the members that I am carrying
                for(int i =0; i<newCarryStatus.length;i++){
                    //replace all c by s
                    if(newCarryStatus[i]=='c')
                        newCarryStatus[i]='s';
                }
            }

        }

        String newCarry = String.valueOf(newCarryStatus);
        nextState = ethanPosX+","+ethanPosY+";"+newCarry;
        return nextState;
    }

    @Override
    public boolean goalTest(String currentState) {
        String[] info = currentState.split(";");
        byte ethanPosX = (byte) Integer.parseInt(info[0].split(",")[0]);
        byte ethanPosY = (byte) Integer.parseInt(info[0].split(",")[1]);
        String carryStatus = info[1];
        if(ethanPosX == submarineX && ethanPosY== submarineY && !carryStatus.contains("c") && !carryStatus.contains("n")){
            return true;
        }
        return false;
    }

    @Override
    public int pathCost(SearchTreeNode node) {
        String state = node.state;
        String[] info = state.split(";");
        String[] ethanPos = info[0].split(",");
        byte ethanX =(byte) Integer.parseInt(ethanPos[0]);
        byte ethanY =(byte) Integer.parseInt(ethanPos[1]);
        String carryStatus = info[1];
        int parentCost;
        if(node.parent==null){
            parentCost=0;
            return parentCost;
        }
        else{
            parentCost = node.parent.cost;
        }
        boolean allCarried=( !carryStatus.contains("n"));
        if(allCarried){
            return parentCost+30;
        }
        int cost=0;
        int numTimeStepsPassed = node.depth;
        String[] healthPoints = IMFHealth.split(",");
        String [] IMFCoordinates = IMFLocations.split(",");
        for(int i=0; i<healthPoints.length;i++){
            boolean incurresDamage=false;
            int health = Integer.parseInt(healthPoints[i]);
            int newHealth=0;
            int damage = 2*numTimeStepsPassed;
            if(carryStatus.charAt(i)=='n'){
                int offset = i*2;
                byte IMFPosX = (byte) Integer.parseInt(IMFCoordinates[offset]);
                byte IMFPosY = (byte) Integer.parseInt(IMFCoordinates[offset + 1]);
                int distance = Math.abs(ethanX - IMFPosX) + Math.abs(ethanY - IMFPosY);
                if(node.operator != 4 || ethanX != IMFPosX || ethanY != IMFPosY) {
                    //i am not carried, I incurred damage
                    newHealth = health + damage;
                    incurresDamage=true;
                }
                if((newHealth==100 && health%2==0)||(newHealth==101 && health%2!=0)){
                    cost+=5000; //death cost is a 2000
                    incurresDamage= false;
                }
                if(newHealth >= 100 && node.operator ==4 && IMFPosX==ethanX &&  IMFPosY==ethanY){ //it means they carry a dead person
                    cost+=3000;
                }
                if(newHealth>=100){
                    incurresDamage=false;
                }

                if(incurresDamage) {
                    int numIMF = healthPoints.length;
                    //a bigger number of IMF members needs a smaller number
                    //this is because at some depth uniform cost begins to favour death over damage because the damage high number from a lot of IMFs makes them close in cost
                    cost += 2*(11-numIMF);
                }
            }

        }

        return cost+parentCost;
    }

    @Override
    public int heuristic(int h, SearchTreeNode node) {
        //search heuristic h(n) is an estimate of the cost of the optimal (cheapest) path from node n to a goal node.
        int estimateOfDamage=0;
        String state = node.state;
        String[] info = state.split(";");
        byte ethanPosX = (byte) Integer.parseInt(info[0].split(",")[0]);
        byte ethanPosY = (byte) Integer.parseInt(info[0].split(",")[1]);
        //boolean atSub = ethanPosX == submarineX && ethanPosY == submarineY;
        String carryStatus = info[1];
        int numCarried=0;
        int numDone=0;
        String[] coordinates = IMFLocations.split(",");
        String[] healthPoints = IMFHealth.split(",");
        int maxDamage=-1;
        int distance=0;
        int maxDistance=0;
            for(int i=0; i<healthPoints.length;i++){
                if(carryStatus.charAt(i)=='n'){
                    int damage = Integer.parseInt(healthPoints[i])+ (2*node.depth);
                    int offset = i*2;
                    byte IMFX = (byte) Integer.parseInt(coordinates[offset]);
                    byte IMFY = (byte) Integer.parseInt(coordinates[offset + 1]);

                    if(h==1) { //manhatten distance
                        distance = Math.abs(ethanPosX - IMFX) + Math.abs(ethanPosY - IMFY);
                    }
                    else{ //eucledian distance
                        distance = (int) (Math.sqrt((ethanPosX - IMFX)^2 +(ethanPosY - IMFY)^2));
                    }
                    //I want to save the person with the highest damage, provided 1. Not dead 2. wont die by the time I get there
                    //I want to minimise the distance from me to them
                    if(damage>maxDamage && damage<100 && damage+(distance*2)<100){
                        maxDamage = damage;
                        maxDistance=distance;
                    }
                    //estimateOfDamage+= distance*damage;
                }
            }
            return maxDistance;
    }

    public static void printGrid(String[][] grid2d){
        for (int i=0;i<grid2d.length;i++){
            for(int j =0; j<grid2d[0].length;j++){
                System.out.print("|");
                if(grid2d[i][j].length()==3)
                    System.out.print("");
                if(grid2d[i][j].length()==0)
                    System.out.print("  ");
                System.out.print(grid2d[i][j]);
                if(grid2d[i][j].length()==2 ||grid2d[i][j].length()==0)
                    System.out.print(" ");
                if(grid2d[i][j].length()==1)
                    System.out.print("  ");
                System.out.print("|");
            }
            System.out.println();
        }
    }


    public void visualiseGrid(ArrayList<SearchTreeNode> pathToGoal){

        for (int i=0; i<pathToGoal.size();i++){
            String[][] grid2d = new String[m][n];
            for (String[] row : grid2d) {
                Arrays.fill(row, "");
            }
            grid2d[submarineX][submarineY]= "SU";
            //get the state
            SearchTreeNode node = pathToGoal.get(i);
            String state = node.state;
            int depth = node.depth;
            //decode state
            String[] info = state.split(";");
            int ethanPosX = Integer.parseInt(info[0].split(",")[0]);
            int ethanPosY = Integer.parseInt(info[0].split(",")[1]);
            String carryStatus = info[1];
            //check if not carried,then display the health
            String[] healthPoints = IMFHealth.split(",");
            String[] IMFCoordinates = IMFLocations.split(",");
            grid2d[ethanPosX][ethanPosY]= "EH";
            for(int j=0; j<healthPoints.length;j++){
                int health = Integer.parseInt(healthPoints[j]);
                int damage = 2*depth;
                if(carryStatus.charAt(j)=='n' /*&& node.operator!=4*/){
                    //i am not carried, I incurred damage
                    health = health+damage;
                    if(health>=100){
                        health =100; //death cost is a 1000
                    }
                    int offset = j*2;
                    byte IMFPosX = (byte) Integer.parseInt(IMFCoordinates[offset]);
                    byte IMFPosY = (byte) Integer.parseInt(IMFCoordinates[offset + 1]);
                    grid2d[IMFPosX][IMFPosY]=health+"";
                }

                grid2d[ethanPosX][ethanPosY]= "EH";

            }
            String op = "";
            switch (node.operator){
                case 0 -> op ="up";
                case 1 -> op ="down";
                case 2 -> op ="Right";
                case 3 -> op ="left";
                case 4 -> op ="carry";
                case 5 -> op ="drop";
            }
            System.out.println("Action: "+op);
            printGrid(grid2d);
            System.out.println();

        }

    }
    public static long bytesToMegabytes(long bytes) {
        final long MEGABYTE = 1024L * 1024L;
        return bytes / MEGABYTE;
    }

    public static void main (String[]args){

        long startTime = System.currentTimeMillis();
        String bigGrid = "15,15;5,10;14,14;0,0,0,1,0,2,0,3,0,4,0,5,0,6,0,7,0,8;81,13,40,38,52,63,66,36,13;3";
        String g15 ="15,15;5,10;14,14;0,0,0,1,0,2,0,3,0,4,0,5,0,6,0,7,0,8;81,13,40,38,52,63,66,36,13;1";
        String smolGrid ="5,5;1,2;4,0;0,3,2,1,3,0,3,2,3,4,4,3;20,30,90,80,70,60;3";
        String grid9 = "9,9;8,7;5,0;0,8,2,6,5,6,1,7,5,5,8,3,2,2,2,5,0,7;11,13,75,50,56,44,26,77,18;8";
        String gridsmol = "6,6;1,1;3,3;3,5,0,1,2,4,4,3,1,5;4,43,94,40,92;3";
        String sambas = "15,15;2,1;1,0;1,3,4,2,4,1,3,1,2,2,10,10,14,12,0,14;50,20,40,32,54,31,39,98;1";
        String randomGrid = "13,13;0,3;3,5;0,1,9,5,7,5,2,2,6,3,6,7,1,7,6,2,5,4;34,85,39,75,62,10,10,99,41;9";//GenGrid();
        //String randomGrid2 = GenGrid();//"15,11;4,9;1,7;0,6,14,1,9,10,2,8,8,10,1,10,12,0,2,4,5,8,14,1,8,10;71,36,34,50,7,85,95,46,59,67,84;7";//GenGrid(); //"5,12;3,3;2,10;2,0,3,5,0,2,0,7,0,7,2,0,3,5,3,4,2,2,4,8,2,11;20,1,61,59,90,49,94,45,30,49,50;9"
        String verysmol = "2,3;0,0;1,0;0,1;40;1";
        String mytestGrid = "10,10;3,0;6,5;1,3,2,1,3,1,4,2,5,5;2,96,20,54,99;1";
        String newGrid ="";//"6,6;1,1;3,3;3,5,0,1,2,4,4,3,1,5;4,43,94,40,92;3"
        String randomGrid2 ="5,5;1,0;2,4;0,1,1,3,2,2;2,90,3;1";//"5,7;3,3;0,4;0,1,2,1,4,4,3,2,3,1,0,2,2,3,3,5,1,4;79,96,29,74,89,62,8,71,33;2";
        String plan2 = solve(mytestGrid,"GR1",true); //437  MY DEPTH  182202 720610
        System.out.println(plan2 );

        Runtime runtime = Runtime.getRuntime();
        // Run the garbage collector
        runtime.gc();
        // Calculate the used memory
        long memory = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Used memory is bytes: " + memory);
        System.out.println("Used memory is megabytes: "
                + bytesToMegabytes(memory));
        //String mytestgrid = "6,6;1,1;3,3;3,5,0,1,2,4,4,3,1,5;4,43,94,40,92;3";

        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;
        System.out.println("Execution time in milliseconds: " + timeElapsed);

    }
}
