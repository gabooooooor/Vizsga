/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package torok.gabriel.vizsga;

/**
 *
 * @author ministar
 */
public abstract class BaseEntity {
    
    private static int count = 0;
    protected String id;
    
    public BaseEntity(String id)
    {
        this.id = id;
        count++;
    }
    
    public String getId()
    {
        return id;
    }
    
    public static int getCount()
    {
        return count;
    }
    
    public abstract String businessKey();
    
    @Override
    public String toString()
    {
        return getClass().getSimpleName() + "[" + businessKey() + "]";
    }
}
