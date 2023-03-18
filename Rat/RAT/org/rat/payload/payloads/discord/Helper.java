//Raddon On Top!

package org.rat.payload.payloads.discord;

public class Helper
{
    public static Checker getChecker() {
        return new Checker();
    }
    
    public static Manager getManager() {
        return new Manager();
    }
    
    public static UserAgents getUserAgents() {
        return new UserAgents();
    }
    
    public static Request getRequest() {
        return new Request();
    }
}
