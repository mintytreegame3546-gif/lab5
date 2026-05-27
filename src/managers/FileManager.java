package managers;

import data.Address;
import data.Coordinates;
import data.Organization;
import data.OrganizationType;

import java.io.*;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Scanner;

public class FileManager {
    private final String path;
    public FileManager(String path) { this.path = path; }

    public void save(LinkedList<Organization> list) {
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(path))) {
            for (Organization o : list) {
                writer.write(o.getId() + ";" + o.getName() + ";" + o.getCoordinates().getX() + ";" +
                        o.getCoordinates().getY() + ";" + o.getCreationDate() + ";" + o.getAnnualTurnover() + ";" +
                        (o.getType() == null ? "" : o.getType()) + ";" +
                        (o.getOfficialAddress().getStreet() == null ? "" : o.getOfficialAddress().getStreet()) + ";" +
                        (o.getOfficialAddress().getZipCode() == null ? "" : o.getOfficialAddress().getZipCode()) + "\n");
            }
            System.out.println("Collection saved.");
        } catch (IOException e) { System.out.println("error saving: " + e.getMessage()); }
    }

    public void load(CollectionManager cm) {
        File file = new File(path);
        if (!file.exists()) return;
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
             Scanner sc = new Scanner(bis)) {
            while (sc.hasNextLine()) {
                String[] p = sc.nextLine().split(";");
                if (p.length < 9) continue;
                long id = Long.parseLong(p[0]);
                Organization org = new Organization(id, p[1], new Coordinates(Long.parseLong(p[2]), Double.parseDouble(p[3])),
                        LocalDateTime.parse(p[4]), Float.parseFloat(p[5]), p[6].isEmpty() ? null : OrganizationType.valueOf(p[6]),
                        new Address(p[7].isEmpty() ? null : p[7], p[8].isEmpty() ? null : p[8]));
                if (isValid(org)) {
                    cm.add(org);
                    cm.setNextId(id);
                }
            }
        } catch (Exception e) { System.out.println("error loading: " + e.getMessage()); }
    }

    private boolean isValid(Organization org) {
        if (org.getId() <= 0) return false;
        if (org.getName() == null || org.getName().trim().isEmpty()) return false;
        if (org.getCoordinates() == null || org.getCoordinates().getX() == null || org.getCoordinates().getY() == null) return false;
        if (org.getCoordinates().getX() > 90L) return false;
        if (org.getCoordinates().getY() > 117.0) return false;
        if (org.getAnnualTurnover() <= 0) return false;
        return org.getOfficialAddress() != null;
    }
}
