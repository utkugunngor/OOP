package project.parts.logics;

import project.SimulationRunner;
import project.components.ProductionLine;
import project.components.Robot;
import project.utility.Common;

public class Inspector extends Logic
{
    @Override public void run ( Robot robot )
    {
        // TODO
        // Following messages are appropriate for this class
        // System.out.printf( "Robot %02d : Detected a broken robot (%02d), adding it to broken robots list.%n", ...);
        // System.out.printf( "Robot %02d : Notifying waiting fixers.%n", ...);

        //synchronized block on robots in order to have correct values about robots since this process is on the robots line
        synchronized (SimulationRunner.factory.robots){
            int i;
            //Loop for finding a robot that lost one of its parts(arm, payload or logic)
            for(i = 0; i < SimulationRunner.factory.robots.size(); i++){
                if((Common.get(SimulationRunner.factory.robots.get(i), "arm") == null ) ||
                        (Common.get(SimulationRunner.factory.robots.get(i), "payload") == null ) ||
                        (Common.get(SimulationRunner.factory.robots.get(i), "logic") == null )){
                    //If there is, it is added to the brokenRobots and synchronized block on brokenRobots for this process.
                    synchronized (SimulationRunner.factory.brokenRobots) {
                        //Message for the detection of a broken robot.
                        synchronized (System.out){
                            System.out.printf( "Robot %02d : Detected a broken robot (%02d), adding it to broken robots list.%n",
                                    Common.get(robot, "serialNo"), Common.get(SimulationRunner.factory.robots.get(i), "serialNo"));
                        }
                        //Add the broken robot to the brokenRobots line.
                        SimulationRunner.factory.brokenRobots.add(SimulationRunner.factory.robots.get(i));
                        //Message for notifying fixers.
                        synchronized (System.out){
                            System.out.printf( "Robot %02d : Notifying waiting fixers.%n",
                                    Common.get(robot, "serialNo"));
                        }
                        //Loop that finds the fixers that are not broken since broken ones will be waiting to be fixed and it does not make sense to wake
                        //up broken fixers. So if a robot is fixer and not broken, update its isWaiting attribute and notify that fixer robot.
                        for (Robot r : SimulationRunner.factory.robots) {
                            synchronized (r) {
                                if(Common.get(r, "logic") instanceof Fixer){
                                    if ((boolean) Common.get(r, "isWaiting") && Common.get(r, "arm") != null
                                        && Common.get(r, "payload") != null && Common.get(r, "logic") != null) {
                                        Common.set(r, "isWaiting", false);
                                        r.notify();
                                    }
                                }
                            }
                        }
                        //After all the process, notify the brokenRobots to release the lock
                        synchronized (SimulationRunner.factory.brokenRobots) {
                            SimulationRunner.factory.brokenRobots.notifyAll();
                        }
                    }
                }
            }
        }
    }
}