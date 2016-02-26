package seniordesign.ratemybusinesspartners;

import android.util.JsonReader;
import java.util.ArrayList;


/**
 * Created by ceenajac on 2/16/2016.
 */
public class Response {
    private String primaryName;
    private String city;
    private String state;
    public Response(){
        this.primaryName = null;
        this.city = null;
        this.state = null;
    }

    public String getPrimaryName() {
        return primaryName;
    }

    public void setPrimaryName(String primaryName) {
        this.primaryName = primaryName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public ArrayList<Response> parseResponse(JsonReader jsonReader) {
        ArrayList<Response> components = null;
        Response component = new Response();
        String result = null;
        String key = null;
        String temp = null;
        int count = 0;
        boolean done = false;
        try {
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String name = jsonReader.nextName();
                if(name.equals("FindCompanyResponse") || name.equals("FindCompanyResponseDetail")){
                    jsonReader.beginObject();
                    name = jsonReader.nextName();
                }
                if (name.equals("FindCandidate")) {
                    // jsonReader.skipValue();
                    jsonReader.beginArray();
                    jsonReader.beginObject();

                    while(jsonReader.hasNext()) {

                        key = jsonReader.nextName();
                        if (key.equals("OrganizationPrimaryName")) {
                            jsonReader.beginObject();
                            temp = jsonReader.nextName();
                            jsonReader.beginObject();
                            temp = jsonReader.nextName();
                            component.setPrimaryName(jsonReader.nextString());
                            jsonReader.endObject();
                            jsonReader.endObject();
                        }else if(key.equals("PrimaryAddress")) {
                            jsonReader.beginObject();
                            while (jsonReader.hasNext()) {
                                key = jsonReader.nextName();
                                if (key.equals("PrimaryTownName")) {
                                    component.setCity(jsonReader.nextString());
                                } else if (key.equals("TerritoryOfficialName")) {
                                    component.setState(jsonReader.nextString());
                                    done = true;
                                } else {
                                    jsonReader.skipValue();
                                }
                            }

                        }else {
                            jsonReader.skipValue();
                        }
                        if(done) {
                            components.add(component);
                        }
                    }
                } else if(name.equals("CandidateReturnedQuantity")) {
                    count = jsonReader.nextInt();
                }else {
                    jsonReader.skipValue();
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            System.out.println(result);
        }
        return components;
    }
}


