package project.parts.logics;

import project.SimulationRunner;
import project.components.Factory;
import project.components.ProductionLine;
import project.components.Robot;
import project.parts.Part;
import project.utility.Common;

public class Supplier extends Logic
{
    @Override public void run ( Robot robot )
    {
        // TODO
        // Following messages are appropriate for this class
        // System.out.printf( "Robot %02d : Supplying a random part on production line.%n", ...);
        // System.out.printf( "Robot %02d : Production line is full, removing a random part from production line.%n", ...);
        // System.out.printf( "Robot %02d : Waking up waiting builders.%n", ...);

        ProductionLine productionLine = SimulationRunner.factory.productionLine;

        //synchronized block on productionLine since when a robot obtains the lock, another one cannot have the lock and unintended results are prevented.
        synchronized (productionLine){

            int productionSize = productionLine.parts.size();
            int productionMax = productionLine.maxCapacity;

            //If there is space for newly coming part, supplier gets a random part by generateRandomPart from Common and add it to the productionLine.
            //Related message is printed according to the action.
            if(productionSize < productionMax){
                synchronized (System.out) {
                    System.out.printf("Robot %02d : Supplying a random part on production line.%n",
                            Common.get(robot, "serialNo"));
                }
                synchronized (productionLine.parts){
                    productionLine.parts.add(Common.generateRandomPart());
                }
            }

            //If there is no space for new parts, supplier generates a random int by Common.random... and removes the part on that index from the productionLine.
            //Related message is printed according to the action.
            else if(productionSize == productionMax){
                synchronized (System.out) {
                    System.out.printf("Robot %02d : Production line is full, removing a random part from production line.%n",
                            Common.get(robot, "serialNo"));
                }
                synchronized (productionLine.parts){
                    int random_part_index = Common.random.nextInt(productionLine.maxCapacity);
                    productionLine.parts.remove(random_part_index);
                }
            }

            //Related message is for waking up the builders.
            System.out.printf( "Robot %02d : Waking up waiting builders.%n", Common.get(robot,"serialNo")) ;

            //synchronized block on robots to change the isWaiting attribute of a specific and correct robot(s) which are instance of Builder class.
            //Checking every robot's logic to see if they are builder type or not.
            synchronized (SimulationRunner.factory.robots){
                for (Robot r : SimulationRunner.factory.robots) {
                    synchronized (r) {
                        if(Common.get(r, "logic") instanceof Builder){
                            if ((boolean) Common.get(r, "isWaiting")) {
                                Common.set(r, "isWaiting", false);
                            }
                        }
                    }
                }
            }
            //Notify all other threads on the production line, they will work according to their isWaiting attribute.
            productionLine.notifyAll();

        }
        //Calling the repaint for the production line after the process.
        synchronized (SimulationRunner.factory.productionLine.parts) {
            SimulationRunner.productionLineDisplay.repaint();
        }
    }
}