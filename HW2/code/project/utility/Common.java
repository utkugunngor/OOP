package project.utility;

import project.SimulationRunner;
import project.components.Factory;
import project.components.ProductionLine;
import project.components.Robot;
import project.parts.Arm;
import project.parts.Base;
import project.parts.Part;
import project.parts.logics.*;
import project.parts.payloads.*;

import java.lang.reflect.Field;
import java.util.Random;

public class Common
{

    public static Random random = new Random() ;


    public static synchronized Object get (Object object , String fieldName )
    {
        // TODO
        // This function retrieves (gets) the private field of an object by using reflection
        // In case the function needs to throw an exception, throw this: SmartFactoryException( "Failed: get!" )
        try {
            Field f = object.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            return f.get(object);
        } catch (IllegalAccessException e) {
            throw new SmartFactoryException("Failed: get!");
        } catch (NoSuchFieldException e) {
            throw new SmartFactoryException("Failed: get!");
        }
    }

    public static synchronized void set ( Object object , String fieldName , Object value )
    {
        // TODO
        // This function modifies (sets) the private field of an object by using reflection
        // In case the function needs to throw an exception, throw this: SmartFactoryException( "Failed: set!" )
        try {
            Field f = object.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(object, value);
        } catch (IllegalAccessException e) {
            throw new SmartFactoryException("Failed: set!");
        } catch (NoSuchFieldException e) {
            throw new SmartFactoryException("Failed: set!");
        }
    }

    //Generate random part for supplier which calls this function when adding new parts to the production line.
    //Fall through for base and arm so that each part(Base, arm, payload, logic) will have a %25 chance to be generated.
    public static Part generateRandomPart() {
        switch (random.nextInt(16)) {
            case 0:
            case 1:
            case 2:
            case 3:
                return Factory.createBase();
            case 4:
            case 5:
            case 6:
            case 7:
                return Factory.createPart("Arm");
            case 8:
                return Factory.createPart("Gripper");
            case 9:
                return Factory.createPart("Welder");
            case 10:
                return Factory.createPart("Camera");
            case 11:
                return Factory.createPart("MaintenanceKit");
            case 12:
                return Factory.createPart("Supplier");
            case 13:
                return Factory.createPart("Builder");
            case 14:
                return Factory.createPart("Inspector");
            case 15:
                return Factory.createPart("Fixer");
            default:
                return Factory.createBase();
        }
    }

    public static Base baseCreate(int nextNo){
        return new Base(nextNo);
    }

    public static Part partCreate(String name){
        try {
            switch (name){
                case "Arm":
                    return new Arm();
                case "Builder":
                    return new Builder();
                case "Fixer":
                    return new Fixer();
                case "Supplier":
                    return new Supplier();
                case "Inspector":
                    return new Inspector();
                case "Gripper":
                    return new Gripper();
                case "Welder":
                    return new Welder();
                case "Camera":
                    return new Camera();
                case "MaintenanceKit":
                    return new MaintenanceKit();
            }
        }
        catch (Exception e){
            throw new SmartFactoryException("Failed: createPart!");
        }
        return null;

    }

}