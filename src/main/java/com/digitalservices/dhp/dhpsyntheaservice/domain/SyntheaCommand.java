package com.digitalservices.dhp.dhpsyntheaservice.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the the command used to run Syntha.  
 */
public class SyntheaCommand {

    private static String POPULATION = "-p";
    private static String SEED = "-s";
    private static String GENDER = "-g";
    private static String AGE_RANGE = "-a";
    private static String OVERFLOW_POPULATION = "-o";
    private static String MODULES = "-m";

    private String populationSize;
    private String seed;
    private String gender;
    private String ageRange;
    private String overflowPopulation;
    private String modules;
    private String state;
    private String city;

    public SyntheaCommand(String populationSize, String seed, String gender, String ageRange, String overflowPopulation,
            String modules, String state, String city) {
        this.populationSize = populationSize;
        this.seed = seed;
        this.gender = gender;
        this.ageRange = ageRange;
        this.overflowPopulation = overflowPopulation;
        this.modules = modules;
        this.state = state;
        this.city = city;
    }

    public List<String> createOptionsString() {
        List<String> options = new ArrayList<>();
        options.add("/bin/sh");
        options.add("run_synthea");
        options.add("--exporter.fhir_stu3.export");
        options.add("true");
        if (this.populationSize != null && !this.populationSize.trim().isEmpty()) {
            options.add(POPULATION);
            options.add(this.populationSize);
        }
        if (this.seed != null && !this.seed.trim().isEmpty()) {
            options.add(SEED);
            options.add(this.seed);
        }
        if (this.gender != null && !this.gender.trim().isEmpty()) {
            options.add(GENDER);
            options.add(this.gender);
        }
        if (this.ageRange != null && !this.ageRange.trim().isEmpty()) {
            options.add(AGE_RANGE);
            options.add(this.ageRange);
        }
        if (this.overflowPopulation != null && !this.overflowPopulation.trim().isEmpty()) {
            options.add(OVERFLOW_POPULATION);
            options.add(this.overflowPopulation);
        }
        if (this.modules != null && !this.modules.trim().isEmpty()) {
            options.add(MODULES);
            options.add(this.modules);
        }
        if (this.state != null && !this.state.trim().isEmpty()) {
            options.add(this.state);
            if (this.city != null && !this.city.trim().isEmpty()) {
                options.add(this.city);
            }
        }

        return options;
    }
    /**
     * 
     * @return the population size
     */
    public String getPopulationSize() {
        return this.populationSize;
    }
    /**
     * 
     * @return the seed
     */
    public String getSeed() {
        return this.seed;
    }
    /**
     * 
     * @return the gender
     */
    public String getGender() {
        return this.gender;
    }
    /**
     * 
     * @return the age range 
     */
    public String getAgeRange() {
        return this.ageRange;
    }

    /**
     * 
     * @return the over flow population
     */
    public String getOverflowPopulation() {
        return this.overflowPopulation;
    }

    /**
     * @return the list of modules
     */
    public String getModules() {
        return modules;
    }

    /**
     * 
     * @return the state
     */
    public String getState() {
        return this.state;
    }

    /**
     * 
     * @return the city
     */
    public String getCity() {
        return this.city;
    }
}
