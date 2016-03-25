package seniordesign.ratemybusinesspartners.models;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;

/**
 * Created by Ryan Comer on 2/12/2016.
 */
public class DummyDatabase {

    public static ArrayList<User> users = new ArrayList<User>();
    public static ArrayList<Review> reviews = new ArrayList<Review>();
    public static HashMap<String, Company> companies = new HashMap<>();

    // Constructor is private, use the static fields instead of instantiating the class
    private DummyDatabase(){}

}
