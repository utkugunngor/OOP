package project.parts.logics;

import project.SimulationRunner;
import project.components.Robot;
import project.parts.payloads.*;
import project.utility.Common;
import sun.applet.Main;

import java.net.CookieHandler;

public class Fixer extends Logic
{
    @Override public void run ( Robot robot )
    {
        // TODO
        // Following messages are appropriate for this class
        // System.out.printf("Robot %02d : Fixed and waken up robot (%02d).%n", ...);
        // System.out.printf("Robot %02d : Nothing to fix, waiting!%n", ...);
        // System.out.printf("Robot %02d : Fixer woke up, going back to work.%n", ...);

        //synchronized block on brokenRobots to make processes with healthy values.
        synchronized (SimulationRunner.factory.brokenRobots){

            int i;
            //Flag that checks if a fix operation has been made.
            boolean fixed = false;

            //If the isWaiting attribute of the fixer is set, fixer prints the corresponding message and waits.
            while((boolean) Common.get(robot, "isWaiting")){
                try{
                    synchronized (System.out){
                        System.out.printf( "Robot %02d : Nothing to fix, waiting!%n",
                                Common.get(robot, "serialNo"));
                    }
                    SimulationRunner.factory.brokenRobots.wait();
                    if(SimulationRunner.factory.stopProduction) break;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //Related message while going back to work.
            synchronized (System.out){
                System.out.printf( "Robot %02d : Fixer woke up, going back to work.%n",
                        Common.get(robot, "serialNo"));
            }

            //Find the first broken robot from brokenRobots
            for(i = 0; i < SimulationRunner.factory.brokenRobots.size(); i++){
                Robot r = SimulationRunner.factory.brokenRobots.get(i);
                //Operation on that specific robot, synchronization block needed
                synchronized (r) {
                    //If arm is broken, create a new arm and attach it.
                    //Set fixed and print corresponding message.
                    //Remove the robot from brokenRobots and notify it to get back to work.
                    //Break after one fix.
                    if (Common.get(r, "arm") == null) {
                        Common.set(r, "arm", SimulationRunner.factory.createPart("Arm"));
                        fixed = true;
                        synchronized (System.out) {
                            System.out.printf("Robot %02d : Fixed and waken up robot (%02d).%n",
                                    Common.get(robot, "serialNo"), Common.get(r, "serialNo"));
                        }
                        SimulationRunner.factory.brokenRobots.remove(r);
                        r.notify();
                        break;
                    }
                    //If payload is broken, create a new payload according to the logic chip that the broken robot has and attach it.
                    //Set fixed and print corresponding message.
                    //Remove the robot from brokenRobots and notify it to get back to work.
                    //Break after one fix.
                    else if (Common.get(r, "payload") == null) {
                        Logic l = (Logic) Common.get(r, "logic");
                        if (l instanceof Supplier)
                            Common.set(r, "payload", SimulationRunner.factory.createPart("Gripper"));
                        else if (l instanceof Builder)
                            Common.set(r, "payload", SimulationRunner.factory.createPart("Welder"));
                        else if (l instanceof Inspector)
                            Common.set(r, "payload", SimulationRunner.factory.createPart("Camera"));
                        else if (l instanceof Fixer)
                            Common.set(r, "payload", SimulationRunner.factory.createPart("MaintenanceKit"));
                        fixed = true;
                        synchronized (System.out) {
                            System.out.printf("Robot %02d : Fixed and waken up robot (%02d).%n",
                                    Common.get(robot, "serialNo"), Common.get(r, "serialNo"));
                        }
                        SimulationRunner.factory.brokenRobots.remove(r);
                        r.notify();
                        break;
                    }
                    //If logic chip is broken, create a logic chip according to the payload that the broken robot has and attach it.
                    //Set fixed and print corresponding message.
                    //Remove the robot from brokenRobots and notify it to get back to work.
                    //Break after one fix.
                    else if (Common.get(r, "logic") == null) {
                        Payload p = (Payload) Common.get(r, "payload");
                        if (p instanceof Gripper)
                            Common.set(r, "logic", SimulationRunner.factory.createPart("Supplier"));
                        else if (p instanceof Welder)
                            Common.set(r, "logic", SimulationRunner.factory.createPart("Builder"));
                        else if (p instanceof Camera)
                            Common.set(r, "logic", SimulationRunner.factory.createPart("Inspector"));
                        else if (p instanceof MaintenanceKit)
                            Common.set(r, "logic", SimulationRunner.factory.createPart("Fixer"));
                        fixed = true;
                        synchronized (System.out) {
                            System.out.printf("Robot %02d : Fixed and waken up robot (%02d).%n",
                                    Common.get(robot, "serialNo"), Common.get(r, "serialNo"));
                        }
                        SimulationRunner.factory.brokenRobots.remove(r);
                        r.notify();
                        break;
                    }
                }
            }
            //If no fix operation has been done, print the related message
            //Fixer waits.
            if(!fixed){
                synchronized (System.out){
                    System.out.printf( "Robot %02d : Nothing to fix, waiting!%n",
                            Common.get(robot, "serialNo"));
                }
                try {
                    SimulationRunner.factory.brokenRobots.wait();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}