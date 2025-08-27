import java.io.*;
import java.util.*;

class CropRecommender {
    static class Crop {
        String name, season, details;
        double minPH, maxPH, minTemp, maxTemp;
        int minRain, maxRain;

        Crop(String name, String season, double minPH, double maxPH, double minTemp, double maxTemp, int minRain, int maxRain, String details) {
            this.name = name;
            this.season = season;
            this.minPH = minPH;
            this.maxPH = maxPH;
            this.minTemp = minTemp;
            this.maxTemp = maxTemp;
            this.minRain = minRain;
            this.maxRain = maxRain;
            this.details = details;
        }
    }

    static List<Crop> crops = new ArrayList<>();

    // Load crop data from CSV
    public static void loadCropsFromCSV(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", 9); // 9 columns
                if (parts.length == 9) {
                    crops.add(new Crop(
                        parts[0], // Name
                        parts[1], // Season
                        Double.parseDouble(parts[2]), // MinPH
                        Double.parseDouble(parts[3]), // MaxPH
                        Double.parseDouble(parts[4]), // MinTemp
                        Double.parseDouble(parts[5]), // MaxTemp
                        Integer.parseInt(parts[6]), // MinRain
                        Integer.parseInt(parts[7]), // MaxRain
                        parts[8]  // Details
                    ));
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading crops: " + e.getMessage());
        }
    }

    // Recommend crops based on input
    public static List<Crop> recommend(double ph, double temp, int rainfall, String season) {
        List<Crop> recommended = new ArrayList<>();
        for (Crop c : crops) {
            if (ph >= c.minPH && ph <= c.maxPH &&
                temp >= c.minTemp && temp <= c.maxTemp &&
                rainfall >= c.minRain && rainfall <= c.maxRain &&
                (c.season.equalsIgnoreCase(season) || c.season.equalsIgnoreCase("Any"))) {
                recommended.add(c);
            }
        }
        return recommended;
    }
}
