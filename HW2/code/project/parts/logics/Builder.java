package project.parts.logics;

import project.SimulationRunner;
import project.components.ProductionLine;
import project.components.Robot;
import project.parts.Arm;
import project.parts.Base;
import project.parts.Part;
import project.parts.payloads.*;
import project.utility.Common;
import sun.rmi.runtime.Log;

public class Builder extends Logic
{
    @Override public void run ( Robot robot )
    {
        // TODO
        // Following messages are appropriate for this class
        // System.out.printf("Robot %02d : Builder cannot build anything, waiting!%n", ...);
        // System.out.printf("Robot %02d : Builder woke up, going back to work.%n", ...);
        // System.out.printf("Robot %02d : Builder attached some parts or relocated a completed robot.%n", ...);

        ProductionLine productionLine = SimulationRunner.factory.productionLine;

        //productionFlag checks if one step is processed
        //found flag is used for attaching correct logic chip. There is a nested loop there, found flag provides the right moment to get out of those loops.
        boolean productionFlag = false, found = false;

        //As in Supplier, synchronized block on productionLine
        //since when a robot obtains the lock, another one cannot have the lock and unintended results are prevented.
        synchronized (productionLine){
            int i, j = productionLine.parts.size();
            //If the builder is not notified by other threads, it waits.
            while((boolean) Common.get(robot, "isWaiting")){
                try{
                    productionLine.wait();
                    if(SimulationRunner.factory.stopProduction) break;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //Message when the builder wakes up.
            synchronized (System.out) {
                System.out.printf("Robot %02d : Builder woke up, going back to work.%n", Common.get(robot, "serialNo"));
            }

            //-----------------Attach an arm to a base----------------------------------
            //If no production is made, try to attach an arm to a base.
            if(!productionFlag){
                //synchronized block on parts in order to obtain correct parts at the found index. Without this block, some issues on indexes might arise.
                //Same synchronization for the other production steps such as attaching a payload to a base-arm etc.
                synchronized (productionLine.parts) {
                    //Loop for finding the first base that does not have an arm.
                    for (i = 0; i < productionLine.parts.size(); i++) {
                        if (productionLine.parts.get(i) instanceof Base) {
                            if (Common.get(productionLine.parts.get(i), "arm") == null) break;
                        }
                    }
                    //If there is a base without an arm, find the first arm to attach that base.
                    if (i < productionLine.parts.size()) {
                        for (j = 0; j < productionLine.parts.size(); j++) {
                            if (productionLine.parts.get(j) instanceof Arm) break;
                        }
                    }
                    //If there is a base and an arm to attach, obtain those by using the indexes and set the arm for that base.
                    //After this process, remove the arm from the production line.
                    //And set the productionFlag to true since only one production step can be made on each run.
                    if (j < productionLine.parts.size() && i < productionLine.parts.size()) {
                        Base base = (Base) productionLine.parts.get(i);
                        Arm arm = (Arm) productionLine.parts.get(j);
                        Common.set(base, "arm", arm);
                        productionLine.parts.remove(arm);
                        productionFlag = true;
                    }
                }
            }

            //---------------Attach a payload to a base-arm-------------------------------
            //If no production is made, try to attach a payload to a base-arm
            if(!productionFlag){
                synchronized (productionLine.parts) {
                    //Loop for finding the first base that has an arm but does not have a payload.
                    for (i = 0; i < productionLine.parts.size(); i++) {
                        if (productionLine.parts.get(i) instanceof Base) {
                            if (Common.get(productionLine.parts.get(i), "arm") != null &&
                                    Common.get(productionLine.parts.get(i), "payload") == null) break;
                        }
                    }
                    //If there is a base with that condition, find the first payload to attach that base-arm.
                    if (i < productionLine.parts.size()) {
                        for (j = 0; j < productionLine.parts.size(); j++) {
                            if (productionLine.parts.get(j) instanceof Payload) break;
                        }
                    }
                    //If there is a base-arm and a payload to attach, obtain those by using the indexes and set the payload for that base-arm.
                    //After this process, remove the payload from the production line.
                    //And set the productionFlag to true since only one production step can be made on each run.
                    if (j < productionLine.parts.size() && i < productionLine.parts.size()) {
                        Base base = (Base) productionLine.parts.get(i);
                        Payload payload = (Payload) productionLine.parts.get(j);
                        Common.set(base, "payload", payload);
                        productionLine.parts.remove(payload);
                        productionFlag = true;
                    }
                }
            }

            //----------------Attach appropriate logic chip to a base-arm with payload-----------------
            //If no production is made, try to attach a correct logic chip
            if(!productionFlag){
                synchronized (productionLine.parts){
                    //Loop for finding the base-arm-payload without a logic chip.
                    //If there is no correct logic chip for the first payload, go for the second etc.
                    //This nested loop checks every possible combination for payloads and logic chips on the production line.
                    for(i = 0; i < productionLine.parts.size() ; i++) {
                        if (productionLine.parts.get(i) instanceof Base) {
                            if (Common.get(productionLine.parts.get(i), "arm") != null &&
                                    Common.get(productionLine.parts.get(i), "payload") != null &&
                                    Common.get(productionLine.parts.get(i), "logic") == null) {
                                //When found, get the current payload from get method of Common.
                                Payload p = (Payload) Common.get(productionLine.parts.get(i), "payload");
                                //This loop checks if there is a correct logic chip that matches the current payload
                                //If so, found flag gets true and break
                                for (j = 0; j < productionLine.parts.size(); j++) {
                                    if (p instanceof Gripper && productionLine.parts.get(j) instanceof Supplier) {
                                        found = true;
                                        break;
                                    }
                                    if (p instanceof Welder && productionLine.parts.get(j) instanceof Builder) {
                                        found = true;
                                        break;
                                    }
                                    if (p instanceof Camera && productionLine.parts.get(j) instanceof Inspector) {
                                        found = true;
                                        break;
                                    }
                                    if (p instanceof MaintenanceKit && productionLine.parts.get(j) instanceof Fixer) {
                                        found = true;
                                        break;
                                    }
                                }
                                //If a correct logic chip-payload match has been found, break
                                if (found) break;
                            }
                        }
                    }
                }
                //If there is a match, obtain those parts from their indexes and set the logic chip of corresponding base.
                //After this process, remove the logic chip from the production line.
                //And set the productionFlag to true since only one production step can be made on each run.
                if(j < productionLine.parts.size() && i < productionLine.parts.size()) {
                    Base base = (Base) productionLine.parts.get(i);
                    Logic logic = (Logic) productionLine.parts.get(j);
                    Common.set(base, "logic", logic );
                    productionLine.parts.remove(logic);
                    productionFlag = true;

                }
            }

            //-------------------Move a completed robot--------------------------
            //If no production is made, try to move a completed robot to the robots line or the storage
            if(!productionFlag){
                synchronized (productionLine.parts){
                    //Loop to find the first completed robot
                    for(i = 0; i < productionLine.parts.size() ; i++){
                        if(productionLine.parts.get(i) instanceof Base){
                            if(Common.get(productionLine.parts.get(i), "arm") != null &&
                                    Common.get(productionLine.parts.get(i), "payload") != null &&
                                    Common.get(productionLine.parts.get(i), "logic") != null) break;
                        }
                    }

                    //If there is a completed robot
                    if (i < productionLine.parts.size()) {
                        //synchronized block on robots. Without this synchronization, another thread might take the lock and robots.size(), indexes or some
                        //other values might change that results in an exception or incorrect results.
                        synchronized (SimulationRunner.factory.robots) {
                                //If there is space in robots line, place the robot on the line, remove it from production line, set the productionFlag
                                //and run this new thread.
                                if (SimulationRunner.factory.robots.size() < SimulationRunner.factory.maxRobots) {
                                    Robot r = (Robot) productionLine.parts.get(i);
                                    SimulationRunner.factory.robots.add(r);
                                    productionLine.parts.remove(r);
                                    productionFlag = true;
                                    new Thread(r).start();
                                }
                            }
                            //If no production is made, that means there is no space in robots, so store it.
                            if(!productionFlag) {
                                //synchronized block on storage.robots, similar reasons to above
                                synchronized (SimulationRunner.factory.storage.robots) {
                                    //If there is space in storage.robots line, place the robot on the line, remove it from production line,
                                    //set the productionFlag
                                    if (SimulationRunner.factory.storage.robots.size() < SimulationRunner.factory.storage.maxCapacity) {
                                        Robot r = (Robot) productionLine.parts.get(i);
                                        SimulationRunner.factory.storage.robots.add(r);
                                        productionLine.parts.remove(r);
                                        productionFlag = true;
                                    }
                                    //After the robot is added to the storage, check if the capacity is full.
                                    //If so stop the production.
                                    if(SimulationRunner.factory.storage.robots.size() == SimulationRunner.factory.storage.maxCapacity){
                                        SimulationRunner.factory.initiateStop();
                                    }
                                }
                            }
                    }
                }
            }

            //If a production has been made, print the corresponding message
            //And repaint all three lines.
            if(productionFlag){
                synchronized (System.out) {
                    System.out.printf("Robot %02d : Builder attached some parts or relocated a completed robot.%n",
                            Common.get(robot, "serialNo"));
                }
                synchronized (productionLine.parts){
                    SimulationRunner.productionLineDisplay.repaint();
                }
                synchronized (SimulationRunner.factory.robots){
                    SimulationRunner.robotsDisplay.repaint();
                }
                synchronized (SimulationRunner.factory.storage.robots){
                    SimulationRunner.storageDisplay.repaint();
                }
            }
            //If not, print the corresponding message and the builder waits.
            else{
                synchronized (System.out) {
                    System.out.printf("Robot %02d : Builder cannot build anything, waiting!%n",
                            Common.get(robot, "serialNo"));
                }
                try {
                    SimulationRunner.factory.productionLine.wait();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }


    }


}