package git.goossensmichael;

import java.util.Arrays;

public class Day4 {

    public static void main(final String[] args) {
        final String[] ranges = INPUT.split("\n");

        {
            final long fullOverlaps = Arrays.stream(ranges)
                    .filter(rangeCouple -> {
                        final String[] sections = rangeCouple.split(",");
                        final int[] firstSection = getSection(sections[0]);
                        final int[] secondSection = getSection(sections[1]);

                        return fullyOverlaps(firstSection, secondSection) || fullyOverlaps(secondSection, firstSection);
                    })
                    .count();

            System.out.println(fullOverlaps);
        }

        {
            final long partialOverlaps = Arrays.stream(ranges)
                    .filter(rangeCouple -> {
                        final String[] sections = rangeCouple.split(",");
                        final int[] firstSection = getSection(sections[0]);
                        final int[] secondSection = getSection(sections[1]);

                        return partiallyOverlaps(firstSection, secondSection);
                    })
                    .count();

            System.out.println(partialOverlaps);
        }

    }

    private static boolean partiallyOverlaps(final int[] firstSection, final int[] secondSection) {
        return firstSection[0] <= secondSection[1] && firstSection[1] >= secondSection[0];
    }

    private static boolean fullyOverlaps(final int[] firstSection, final int[] secondSection) {
        return firstSection[0] <= secondSection[0] && firstSection[1] >= secondSection[1];
    }

    private static int[] getSection(final String range) {
        final String[] bounds = range.split("-");

        return new int[]{Integer.parseInt(bounds[0]), Integer.parseInt(bounds[1])};
    }

    private static final String INPUT = "16-80,80-87\n" +
            "4-9,10-97\n" +
            "6-94,93-93\n" +
            "31-73,8-73\n" +
            "4-72,5-73\n" +
            "6-63,4-5\n" +
            "3-44,4-45\n" +
            "89-96,95-95\n" +
            "56-95,29-95\n" +
            "5-22,5-22\n" +
            "49-63,48-63\n" +
            "97-97,2-98\n" +
            "1-13,3-63\n" +
            "63-93,64-94\n" +
            "41-63,64-76\n" +
            "24-45,46-72\n" +
            "28-94,29-78\n" +
            "18-36,35-37\n" +
            "83-85,8-84\n" +
            "22-35,21-22\n" +
            "18-48,18-18\n" +
            "98-98,1-99\n" +
            "88-94,94-96\n" +
            "21-93,74-93\n" +
            "63-83,62-82\n" +
            "89-90,15-90\n" +
            "63-80,61-79\n" +
            "12-14,19-63\n" +
            "30-30,31-78\n" +
            "7-88,87-88\n" +
            "27-41,27-27\n" +
            "6-35,6-36\n" +
            "3-78,77-79\n" +
            "4-67,22-57\n" +
            "19-82,65-87\n" +
            "3-93,6-46\n" +
            "18-30,31-41\n" +
            "14-95,13-94\n" +
            "33-59,17-33\n" +
            "37-38,38-96\n" +
            "1-5,4-90\n" +
            "64-79,39-84\n" +
            "59-62,1-37\n" +
            "15-29,2-9\n" +
            "38-44,37-43\n" +
            "8-75,9-54\n" +
            "77-89,7-77\n" +
            "2-87,2-86\n" +
            "7-81,6-6\n" +
            "59-94,93-93\n" +
            "27-75,9-27\n" +
            "24-76,23-75\n" +
            "2-10,28-63\n" +
            "60-61,17-60\n" +
            "76-76,2-75\n" +
            "12-60,13-61\n" +
            "39-96,95-95\n" +
            "31-88,88-94\n" +
            "6-79,35-66\n" +
            "51-93,58-94\n" +
            "40-74,23-40\n" +
            "12-62,13-65\n" +
            "30-49,49-69\n" +
            "36-98,10-98\n" +
            "29-95,9-95\n" +
            "8-79,79-86\n" +
            "1-73,2-19\n" +
            "8-15,14-16\n" +
            "6-34,1-5\n" +
            "38-58,37-59\n" +
            "58-84,62-84\n" +
            "26-26,25-27\n" +
            "5-89,47-88\n" +
            "36-50,34-49\n" +
            "59-94,58-95\n" +
            "67-71,68-70\n" +
            "3-64,3-65\n" +
            "65-93,94-94\n" +
            "38-91,75-95\n" +
            "97-97,6-98\n" +
            "32-78,33-79\n" +
            "20-89,19-90\n" +
            "34-95,35-95\n" +
            "3-99,2-97\n" +
            "29-90,89-89\n" +
            "31-48,32-49\n" +
            "63-81,51-72\n" +
            "6-96,98-99\n" +
            "8-94,27-94\n" +
            "7-99,3-99\n" +
            "19-90,18-91\n" +
            "28-97,27-88\n" +
            "76-86,40-75\n" +
            "7-65,6-8\n" +
            "2-12,13-26\n" +
            "83-88,60-86\n" +
            "54-99,54-97\n" +
            "6-7,6-97\n" +
            "11-78,77-77\n" +
            "3-99,3-99\n" +
            "21-21,21-88\n" +
            "11-20,2-50\n" +
            "53-88,89-98\n" +
            "52-97,52-98\n" +
            "8-29,4-32\n" +
            "88-89,6-89\n" +
            "7-62,6-7\n" +
            "89-98,8-89\n" +
            "32-91,3-91\n" +
            "39-49,48-50\n" +
            "8-63,9-64\n" +
            "23-80,22-79\n" +
            "44-66,16-65\n" +
            "44-58,38-58\n" +
            "71-98,70-97\n" +
            "4-99,5-99\n" +
            "8-90,5-73\n" +
            "51-98,77-96\n" +
            "26-89,25-25\n" +
            "8-25,14-65\n" +
            "39-64,45-65\n" +
            "30-94,59-92\n" +
            "37-96,3-38\n" +
            "45-72,22-44\n" +
            "2-95,98-98\n" +
            "35-35,34-34\n" +
            "16-82,14-44\n" +
            "66-79,65-78\n" +
            "71-72,72-94\n" +
            "14-21,13-20\n" +
            "14-92,1-14\n" +
            "24-69,68-88\n" +
            "21-80,79-81\n" +
            "56-86,69-85\n" +
            "48-85,49-85\n" +
            "47-90,90-91\n" +
            "7-22,11-27\n" +
            "97-98,24-92\n" +
            "7-91,90-91\n" +
            "46-58,58-69\n" +
            "83-92,91-98\n" +
            "27-66,27-66\n" +
            "13-92,12-97\n" +
            "84-86,17-79\n" +
            "55-91,54-56\n" +
            "2-87,2-88\n" +
            "46-46,46-78\n" +
            "97-97,92-98\n" +
            "24-47,25-46\n" +
            "4-22,21-27\n" +
            "76-87,11-77\n" +
            "17-87,16-18\n" +
            "50-81,49-87\n" +
            "93-98,29-93\n" +
            "19-44,20-43\n" +
            "18-53,53-57\n" +
            "9-45,9-46\n" +
            "7-55,33-55\n" +
            "22-25,25-81\n" +
            "96-96,75-97\n" +
            "9-11,10-99\n" +
            "27-73,72-74\n" +
            "10-72,71-71\n" +
            "65-66,64-66\n" +
            "27-81,86-96\n" +
            "33-37,37-68\n" +
            "26-90,46-90\n" +
            "1-4,4-97\n" +
            "9-95,10-95\n" +
            "6-32,7-72\n" +
            "68-89,12-95\n" +
            "2-98,13-99\n" +
            "89-91,55-90\n" +
            "7-23,22-67\n" +
            "11-11,11-68\n" +
            "86-90,72-91\n" +
            "33-53,29-29\n" +
            "80-91,6-80\n" +
            "15-87,27-73\n" +
            "1-99,1-99\n" +
            "68-69,64-95\n" +
            "17-62,45-61\n" +
            "7-26,26-30\n" +
            "64-96,95-97\n" +
            "17-18,18-58\n" +
            "20-97,98-98\n" +
            "4-87,5-87\n" +
            "15-19,18-19\n" +
            "66-67,23-66\n" +
            "13-95,9-94\n" +
            "14-81,82-85\n" +
            "12-85,8-85\n" +
            "4-91,3-4\n" +
            "98-99,31-95\n" +
            "3-77,2-76\n" +
            "15-97,5-15\n" +
            "45-72,71-73\n" +
            "3-70,2-4\n" +
            "81-97,18-95\n" +
            "68-83,17-84\n" +
            "1-3,4-77\n" +
            "33-92,91-91\n" +
            "31-91,30-32\n" +
            "45-64,46-65\n" +
            "23-94,89-95\n" +
            "5-76,5-58\n" +
            "5-86,3-86\n" +
            "31-69,8-69\n" +
            "66-69,66-68\n" +
            "50-64,63-64\n" +
            "1-97,99-99\n" +
            "76-77,75-80\n" +
            "90-97,88-97\n" +
            "22-52,11-30\n" +
            "40-70,40-98\n" +
            "2-96,42-99\n" +
            "4-49,1-59\n" +
            "20-75,74-76\n" +
            "23-52,42-66\n" +
            "3-94,92-96\n" +
            "4-93,2-3\n" +
            "4-92,3-92\n" +
            "57-75,70-76\n" +
            "4-95,2-5\n" +
            "1-41,2-8\n" +
            "94-99,2-81\n" +
            "45-63,10-63\n" +
            "3-8,9-45\n" +
            "17-50,17-49\n" +
            "43-76,42-76\n" +
            "2-86,85-87\n" +
            "27-29,28-42\n" +
            "74-80,37-53\n" +
            "19-97,13-99\n" +
            "9-31,9-31\n" +
            "37-39,38-59\n" +
            "26-79,15-91\n" +
            "85-85,31-86\n" +
            "99-99,5-96\n" +
            "10-39,9-16\n" +
            "22-60,22-59\n" +
            "13-95,1-12\n" +
            "91-93,23-93\n" +
            "77-98,19-78\n" +
            "53-93,93-98\n" +
            "78-80,27-79\n" +
            "23-25,23-24\n" +
            "12-62,11-13\n" +
            "4-48,1-73\n" +
            "6-63,6-63\n" +
            "35-86,34-36\n" +
            "5-27,4-27\n" +
            "23-73,5-83\n" +
            "28-34,21-38\n" +
            "36-93,93-97\n" +
            "16-84,17-84\n" +
            "12-99,11-80\n" +
            "33-81,20-32\n" +
            "76-93,50-98\n" +
            "30-82,33-93\n" +
            "1-99,1-99\n" +
            "61-68,32-67\n" +
            "20-84,20-83\n" +
            "7-13,6-13\n" +
            "51-52,7-52\n" +
            "27-67,65-65\n" +
            "1-91,90-91\n" +
            "81-98,86-98\n" +
            "93-97,48-93\n" +
            "4-53,5-53\n" +
            "68-76,18-74\n" +
            "5-21,1-6\n" +
            "19-97,18-98\n" +
            "2-65,2-66\n" +
            "4-72,4-73\n" +
            "5-92,65-75\n" +
            "9-77,23-76\n" +
            "17-89,17-89\n" +
            "57-60,55-59\n" +
            "2-90,1-1\n" +
            "28-81,28-81\n" +
            "34-56,48-85\n" +
            "2-97,97-99\n" +
            "42-43,43-99\n" +
            "17-60,15-19\n" +
            "62-99,6-62\n" +
            "32-41,25-44\n" +
            "4-37,7-36\n" +
            "22-72,71-73\n" +
            "24-32,24-33\n" +
            "44-78,44-44\n" +
            "1-32,32-65\n" +
            "47-95,14-95\n" +
            "47-77,46-56\n" +
            "5-97,3-99\n" +
            "20-99,21-98\n" +
            "9-65,8-65\n" +
            "88-96,23-96\n" +
            "15-64,9-64\n" +
            "1-24,25-64\n" +
            "71-91,90-91\n" +
            "51-52,39-53\n" +
            "10-57,9-19\n" +
            "8-86,7-87\n" +
            "3-7,8-78\n" +
            "13-17,16-49\n" +
            "6-90,7-72\n" +
            "6-86,4-85\n" +
            "17-93,16-94\n" +
            "7-83,82-83\n" +
            "33-81,32-62\n" +
            "13-99,19-99\n" +
            "26-98,88-96\n" +
            "48-98,97-98\n" +
            "36-50,49-50\n" +
            "1-75,74-75\n" +
            "15-95,2-15\n" +
            "51-58,16-51\n" +
            "1-98,6-98\n" +
            "2-99,10-90\n" +
            "13-97,96-96\n" +
            "50-70,43-70\n" +
            "15-33,14-16\n" +
            "8-89,7-44\n" +
            "44-91,40-44\n" +
            "93-94,2-96\n" +
            "4-88,5-89\n" +
            "15-73,73-74\n" +
            "96-97,97-98\n" +
            "39-57,56-85\n" +
            "65-67,47-66\n" +
            "26-27,27-78\n" +
            "57-80,57-80\n" +
            "97-99,2-98\n" +
            "18-18,18-18\n" +
            "27-47,27-48\n" +
            "86-93,90-97\n" +
            "19-47,10-48\n" +
            "62-78,28-78\n" +
            "93-99,3-93\n" +
            "55-59,55-78\n" +
            "98-99,89-91\n" +
            "34-35,7-35\n" +
            "78-88,21-79\n" +
            "26-92,15-33\n" +
            "57-64,38-57\n" +
            "47-92,91-91\n" +
            "14-38,13-13\n" +
            "96-99,40-96\n" +
            "55-84,25-82\n" +
            "56-76,73-86\n" +
            "10-82,26-81\n" +
            "30-89,31-89\n" +
            "24-68,25-68\n" +
            "11-44,12-45\n" +
            "33-49,11-49\n" +
            "58-58,18-57\n" +
            "23-84,23-85\n" +
            "3-31,2-2\n" +
            "50-96,41-96\n" +
            "5-98,1-6\n" +
            "10-97,11-97\n" +
            "36-91,38-91\n" +
            "9-88,20-87\n" +
            "6-96,6-97\n" +
            "28-87,74-87\n" +
            "6-77,1-78\n" +
            "6-73,6-74\n" +
            "66-93,18-85\n" +
            "5-80,66-80\n" +
            "12-95,8-95\n" +
            "68-89,18-68\n" +
            "6-15,14-16\n" +
            "4-87,4-88\n" +
            "50-50,24-54\n" +
            "96-97,3-96\n" +
            "3-46,12-50\n" +
            "77-87,29-34\n" +
            "32-53,52-91\n" +
            "10-68,5-68\n" +
            "40-54,47-54\n" +
            "8-22,9-16\n" +
            "96-99,2-97\n" +
            "10-63,6-9\n" +
            "22-65,64-65\n" +
            "88-89,87-90\n" +
            "15-99,98-98\n" +
            "89-89,66-90\n" +
            "9-65,4-10\n" +
            "36-61,39-39\n" +
            "78-99,70-78\n" +
            "85-87,86-97\n" +
            "3-73,67-82\n" +
            "3-68,68-69\n" +
            "14-15,15-90\n" +
            "42-59,43-59\n" +
            "15-60,3-4\n" +
            "25-92,29-79\n" +
            "19-99,18-18\n" +
            "3-88,10-76\n" +
            "61-98,97-98\n" +
            "2-92,1-7\n" +
            "24-86,17-24\n" +
            "82-82,3-83\n" +
            "23-25,20-26\n" +
            "4-65,1-66\n" +
            "68-72,34-76\n" +
            "24-93,25-94\n" +
            "17-47,18-48\n" +
            "30-65,30-65\n" +
            "71-77,75-76\n" +
            "24-64,58-88\n" +
            "4-30,31-31\n" +
            "4-65,5-66\n" +
            "12-65,64-64\n" +
            "52-52,51-81\n" +
            "67-69,2-68\n" +
            "6-81,8-81\n" +
            "53-88,52-53\n" +
            "70-98,69-98\n" +
            "13-15,14-79\n" +
            "15-71,14-86\n" +
            "31-65,31-64\n" +
            "59-86,5-86\n" +
            "8-9,8-15\n" +
            "94-96,63-78\n" +
            "28-49,27-82\n" +
            "3-3,3-95\n" +
            "74-74,75-96\n" +
            "82-96,97-97\n" +
            "18-83,6-98\n" +
            "1-74,3-41\n" +
            "25-87,24-88\n" +
            "42-96,95-96\n" +
            "25-89,26-35\n" +
            "35-61,35-61\n" +
            "20-39,22-35\n" +
            "4-65,3-5\n" +
            "48-97,46-67\n" +
            "37-73,38-73\n" +
            "42-45,42-43\n" +
            "54-85,84-85\n" +
            "23-61,20-23\n" +
            "23-60,22-28\n" +
            "41-63,23-41\n" +
            "68-90,69-89\n" +
            "12-97,2-33\n" +
            "8-83,25-83\n" +
            "6-12,5-15\n" +
            "37-67,31-68\n" +
            "1-2,5-81\n" +
            "63-89,62-62\n" +
            "63-90,4-63\n" +
            "8-95,1-7\n" +
            "47-72,41-44\n" +
            "93-94,19-81\n" +
            "86-88,25-87\n" +
            "57-57,57-88\n" +
            "13-94,13-93\n" +
            "16-17,15-16\n" +
            "61-80,58-81\n" +
            "4-96,3-4\n" +
            "12-28,31-54\n" +
            "57-96,56-57\n" +
            "16-89,17-19\n" +
            "71-87,70-90\n" +
            "79-93,78-92\n" +
            "31-38,32-70\n" +
            "16-17,17-75\n" +
            "76-92,91-95\n" +
            "77-77,56-76\n" +
            "63-90,62-66\n" +
            "6-86,2-81\n" +
            "3-42,4-41\n" +
            "38-94,93-98\n" +
            "52-60,52-61\n" +
            "17-84,16-84\n" +
            "5-5,13-90\n" +
            "25-25,25-39\n" +
            "22-91,22-90\n" +
            "11-76,10-11\n" +
            "9-45,2-45\n" +
            "70-77,69-76\n" +
            "6-10,5-7\n" +
            "36-36,37-42\n" +
            "12-96,54-98\n" +
            "2-96,1-1\n" +
            "2-94,5-94\n" +
            "32-84,83-88\n" +
            "84-84,84-96\n" +
            "17-54,53-54\n" +
            "1-68,14-69\n" +
            "9-98,8-97\n" +
            "1-88,85-94\n" +
            "40-69,39-39\n" +
            "5-21,21-76\n" +
            "27-28,5-28\n" +
            "2-80,2-81\n" +
            "15-96,9-15\n" +
            "87-89,2-89\n" +
            "19-84,33-92\n" +
            "32-95,11-95\n" +
            "38-53,17-37\n" +
            "67-89,67-90\n" +
            "4-5,5-60\n" +
            "45-84,83-88\n" +
            "9-32,77-86\n" +
            "45-72,16-65\n" +
            "23-24,23-92\n" +
            "8-93,92-92\n" +
            "7-91,7-90\n" +
            "45-48,47-78\n" +
            "58-58,4-59\n" +
            "86-91,13-87\n" +
            "79-85,28-79\n" +
            "12-13,10-16\n" +
            "62-99,98-98\n" +
            "25-96,25-96\n" +
            "16-57,56-79\n" +
            "38-77,38-76\n" +
            "82-97,68-83\n" +
            "7-66,56-63\n" +
            "5-7,6-95\n" +
            "15-93,8-15\n" +
            "9-93,10-93\n" +
            "74-90,51-91\n" +
            "69-75,63-80\n" +
            "4-11,10-91\n" +
            "36-99,33-53\n" +
            "8-8,9-95\n" +
            "94-96,95-96\n" +
            "1-38,37-38\n" +
            "66-77,67-78\n" +
            "65-87,87-95\n" +
            "35-37,36-48\n" +
            "55-92,54-93\n" +
            "20-21,19-21\n" +
            "76-76,77-98\n" +
            "39-69,44-57\n" +
            "6-26,25-54\n" +
            "18-37,36-37\n" +
            "34-74,45-57\n" +
            "60-83,98-99\n" +
            "92-94,3-93\n" +
            "13-59,12-72\n" +
            "48-98,6-87\n" +
            "17-88,8-87\n" +
            "24-92,23-93\n" +
            "98-98,4-94\n" +
            "11-57,8-8\n" +
            "65-87,12-87\n" +
            "25-99,25-98\n" +
            "18-88,18-87\n" +
            "34-80,33-79\n" +
            "45-96,5-97\n" +
            "33-67,20-33\n" +
            "2-98,97-98\n" +
            "32-33,33-90\n" +
            "85-90,85-91\n" +
            "94-94,38-95\n" +
            "93-93,42-92\n" +
            "38-48,13-78\n" +
            "3-95,25-89\n" +
            "17-93,16-16\n" +
            "57-70,39-53\n" +
            "11-61,7-10\n" +
            "39-89,6-39\n" +
            "7-17,16-97\n" +
            "30-95,56-94\n" +
            "2-8,8-23\n" +
            "4-17,17-45\n" +
            "77-82,73-77\n" +
            "21-84,8-29\n" +
            "35-91,81-97\n" +
            "5-54,3-9\n" +
            "1-94,2-95\n" +
            "7-66,67-96\n" +
            "21-45,15-22\n" +
            "6-45,11-44\n" +
            "52-87,29-51\n" +
            "15-82,46-81\n" +
            "74-76,45-75\n" +
            "95-99,20-93\n" +
            "40-89,24-52\n" +
            "20-99,20-99\n" +
            "5-34,35-97\n" +
            "27-45,44-62\n" +
            "8-75,2-8\n" +
            "50-55,43-49\n" +
            "15-50,19-50\n" +
            "13-71,10-71\n" +
            "45-64,44-45\n" +
            "88-92,47-88\n" +
            "33-68,33-52\n" +
            "39-79,31-88\n" +
            "31-58,95-97\n" +
            "22-31,2-31\n" +
            "36-88,37-89\n" +
            "45-93,44-92\n" +
            "6-97,96-98\n" +
            "34-72,10-82\n" +
            "54-58,31-58\n" +
            "96-98,6-96\n" +
            "45-46,46-96\n" +
            "56-69,55-68\n" +
            "14-54,13-53\n" +
            "25-45,18-81\n" +
            "54-55,40-55\n" +
            "94-98,72-94\n" +
            "7-91,6-92\n" +
            "69-96,68-95\n" +
            "42-82,41-46\n" +
            "64-94,13-91\n" +
            "52-80,52-81\n" +
            "39-69,15-73\n" +
            "32-53,33-52\n" +
            "11-22,11-21\n" +
            "15-79,14-69\n" +
            "44-65,51-66\n" +
            "1-32,4-26\n" +
            "36-36,6-35\n" +
            "65-71,65-72\n" +
            "49-74,23-74\n" +
            "27-96,74-96\n" +
            "12-70,13-71\n" +
            "64-66,7-66\n" +
            "17-97,18-99\n" +
            "30-30,31-95\n" +
            "94-96,90-94\n" +
            "1-15,15-15\n" +
            "11-45,7-10\n" +
            "14-93,15-93\n" +
            "19-47,20-44\n" +
            "9-97,8-98\n" +
            "6-62,61-63\n" +
            "21-51,50-63\n" +
            "4-95,2-95\n" +
            "54-55,55-58\n" +
            "4-62,4-63\n" +
            "18-88,18-87\n" +
            "4-79,5-79\n" +
            "92-97,10-93\n" +
            "9-79,20-78\n" +
            "44-87,26-91\n" +
            "7-51,10-51\n" +
            "43-92,62-95\n" +
            "16-44,16-45\n" +
            "5-98,4-99\n" +
            "20-64,19-63\n" +
            "2-20,1-19\n" +
            "12-96,12-95\n" +
            "13-94,95-95\n" +
            "16-46,8-87\n" +
            "5-24,25-54\n" +
            "4-12,6-18\n" +
            "32-66,31-37\n" +
            "24-76,56-96\n" +
            "5-51,4-51\n" +
            "55-60,54-55\n" +
            "11-74,6-7\n" +
            "6-99,53-68\n" +
            "15-90,88-91\n" +
            "27-84,28-83\n" +
            "14-67,3-98\n" +
            "41-56,30-54\n" +
            "29-52,29-51\n" +
            "83-89,12-85\n" +
            "47-76,40-64\n" +
            "18-70,18-18\n" +
            "5-20,6-21\n" +
            "26-53,25-53\n" +
            "36-48,49-92\n" +
            "48-80,47-48\n" +
            "51-63,17-58\n" +
            "9-79,47-80\n" +
            "38-39,7-39\n" +
            "38-58,23-51\n" +
            "19-37,36-37\n" +
            "1-61,1-60\n" +
            "9-65,8-65\n" +
            "15-17,16-95\n" +
            "50-66,50-79\n" +
            "18-39,17-18\n" +
            "7-14,13-98\n" +
            "5-80,5-80\n" +
            "12-98,12-12\n" +
            "5-6,6-99\n" +
            "72-91,24-95\n" +
            "46-92,20-96\n" +
            "27-73,26-49\n" +
            "7-52,52-98\n" +
            "20-57,2-56\n" +
            "1-69,6-68\n" +
            "52-95,53-95\n" +
            "58-71,58-65\n" +
            "20-86,20-86\n" +
            "50-71,49-49\n" +
            "9-79,8-78\n" +
            "45-62,25-45\n" +
            "15-23,18-24\n" +
            "4-96,4-95\n" +
            "13-97,12-97\n" +
            "1-99,15-96\n" +
            "52-54,53-54\n" +
            "7-10,9-42\n" +
            "12-79,11-78\n" +
            "1-88,1-89\n" +
            "23-95,24-94\n" +
            "5-96,5-96\n" +
            "27-73,64-73\n" +
            "31-97,74-97\n" +
            "6-74,74-90\n" +
            "29-91,14-30\n" +
            "16-36,7-37\n" +
            "70-83,69-82\n" +
            "58-96,30-97\n" +
            "9-89,51-64\n" +
            "63-88,87-99\n" +
            "9-22,9-21\n" +
            "25-99,22-22\n" +
            "60-60,21-61\n" +
            "31-83,32-88\n" +
            "31-65,64-97\n" +
            "9-9,9-91\n" +
            "10-47,9-21\n" +
            "14-97,60-83\n" +
            "5-74,5-73\n" +
            "4-7,6-89\n" +
            "1-94,50-92\n" +
            "12-69,11-12\n" +
            "75-84,80-85\n" +
            "27-38,18-39\n" +
            "11-27,4-12\n" +
            "93-96,5-93\n" +
            "56-88,88-97\n" +
            "45-99,20-95\n" +
            "21-93,94-94\n" +
            "16-96,15-16\n" +
            "86-92,6-84\n" +
            "10-63,3-7\n" +
            "23-59,58-69\n" +
            "10-13,12-12\n" +
            "15-97,14-86\n" +
            "15-98,4-50\n" +
            "30-30,22-29\n" +
            "44-58,45-58\n" +
            "85-87,42-86\n" +
            "58-59,16-59\n" +
            "14-63,8-14\n" +
            "48-64,48-64\n" +
            "1-34,1-33\n" +
            "75-99,76-98\n" +
            "8-60,1-8\n" +
            "3-8,7-9\n" +
            "22-70,1-71\n" +
            "38-93,93-99\n" +
            "91-93,62-92\n" +
            "95-95,6-96\n" +
            "4-92,4-92\n" +
            "35-99,36-84\n" +
            "53-94,51-51\n" +
            "27-93,27-94\n" +
            "3-5,4-55\n" +
            "88-94,44-94\n" +
            "79-95,85-94\n" +
            "11-23,23-46\n" +
            "62-88,89-89\n" +
            "1-84,1-85\n" +
            "84-86,85-89\n" +
            "1-99,1-99\n" +
            "64-81,46-64\n" +
            "25-26,25-31\n" +
            "17-92,3-4\n" +
            "7-51,16-24\n" +
            "17-97,85-99\n" +
            "5-5,6-91\n" +
            "73-91,90-91\n" +
            "18-65,49-64\n" +
            "1-93,92-92\n" +
            "5-97,96-97\n" +
            "7-14,13-14\n" +
            "42-73,42-86\n" +
            "40-48,34-48\n" +
            "22-94,21-93\n" +
            "5-71,70-70\n" +
            "74-92,11-91\n" +
            "90-92,5-91\n" +
            "53-58,16-59\n" +
            "20-78,8-78\n" +
            "3-5,4-4\n" +
            "48-50,49-95\n" +
            "71-88,44-45\n" +
            "30-39,7-30\n" +
            "11-69,40-69\n" +
            "7-85,82-90\n" +
            "75-79,53-67\n" +
            "6-84,83-83\n" +
            "86-95,88-96\n" +
            "3-46,2-47\n" +
            "3-86,86-94\n" +
            "50-98,46-50\n" +
            "24-45,23-24\n" +
            "44-52,4-91\n" +
            "38-67,67-99\n" +
            "70-93,34-94\n" +
            "40-86,40-40\n" +
            "5-71,9-58\n" +
            "31-71,31-70\n" +
            "11-50,49-64\n" +
            "34-39,35-45\n" +
            "45-72,38-44\n" +
            "68-73,72-72\n" +
            "63-64,15-64\n" +
            "91-91,31-90\n" +
            "15-86,10-85\n" +
            "6-17,7-8\n" +
            "7-96,53-99\n" +
            "88-88,28-89\n" +
            "22-65,18-22\n" +
            "13-95,99-99\n" +
            "23-99,22-99\n" +
            "55-62,33-55\n" +
            "8-85,47-84\n" +
            "13-81,14-22\n" +
            "7-90,92-99\n" +
            "11-47,13-77\n" +
            "38-64,20-65\n" +
            "58-61,2-62\n" +
            "19-27,11-27\n" +
            "30-43,29-34\n" +
            "74-95,93-95\n" +
            "66-76,62-87\n" +
            "88-88,89-89\n" +
            "45-80,45-80\n" +
            "31-76,31-87\n" +
            "37-84,37-37\n" +
            "81-81,66-80\n" +
            "41-85,19-42\n" +
            "19-47,5-18\n" +
            "50-75,25-75\n" +
            "91-92,46-91\n" +
            "65-84,3-47\n" +
            "32-34,32-33\n" +
            "36-60,5-61\n" +
            "11-98,12-99\n" +
            "27-74,27-74\n" +
            "4-41,31-93\n" +
            "46-76,45-74\n" +
            "12-38,26-38\n" +
            "5-80,5-80\n" +
            "5-99,6-99\n" +
            "49-90,49-49\n" +
            "5-85,1-4\n" +
            "3-99,4-28\n" +
            "21-39,1-21\n" +
            "46-86,45-69\n" +
            "73-92,73-91\n" +
            "2-97,45-97\n" +
            "1-81,3-60\n" +
            "44-78,17-78\n" +
            "11-78,77-79\n" +
            "18-51,52-75\n" +
            "20-82,20-82\n" +
            "43-55,4-43\n" +
            "2-33,3-33\n" +
            "11-16,4-10\n" +
            "26-34,27-34\n" +
            "85-94,93-93\n" +
            "20-30,13-31\n" +
            "42-90,42-89\n" +
            "30-45,46-93\n" +
            "5-99,4-95\n" +
            "51-51,51-52\n" +
            "95-96,38-96\n" +
            "20-72,19-98\n" +
            "31-86,30-86\n" +
            "42-82,43-83\n" +
            "15-89,21-90\n" +
            "5-98,5-98\n" +
            "7-96,37-98\n" +
            "28-29,28-62\n" +
            "4-89,36-89\n" +
            "2-98,71-97\n" +
            "28-90,8-17\n" +
            "72-73,45-73\n" +
            "95-96,82-96\n" +
            "4-60,59-77\n" +
            "20-63,64-64\n" +
            "2-24,2-46\n" +
            "26-67,26-68\n" +
            "52-53,32-53\n" +
            "24-99,79-88\n" +
            "60-62,23-61\n" +
            "2-96,3-97\n" +
            "13-92,29-71\n" +
            "1-2,1-85\n" +
            "9-77,44-99\n" +
            "1-96,3-96\n" +
            "1-68,4-67\n" +
            "52-60,12-61\n" +
            "10-90,10-90\n" +
            "66-69,41-69\n" +
            "99-99,35-97\n" +
            "15-95,14-95\n" +
            "93-95,80-92\n" +
            "7-41,8-42\n" +
            "1-94,95-95\n" +
            "3-32,1-32\n" +
            "80-83,17-84\n" +
            "53-69,68-91\n" +
            "86-90,2-87\n" +
            "73-87,28-86\n" +
            "8-35,7-34\n" +
            "74-88,36-74\n" +
            "45-45,44-68\n" +
            "67-88,9-66\n" +
            "54-90,29-91\n" +
            "13-45,44-94\n" +
            "70-71,24-71\n" +
            "82-96,81-97\n" +
            "43-73,42-73\n" +
            "8-45,39-44\n" +
            "5-42,6-43\n" +
            "54-93,5-55\n" +
            "75-96,75-96\n" +
            "11-31,3-7\n" +
            "82-90,81-82\n" +
            "33-77,54-57\n" +
            "77-79,73-79\n" +
            "34-90,33-56\n" +
            "4-13,13-49\n" +
            "3-19,18-97\n" +
            "3-97,2-99\n" +
            "28-51,19-29\n" +
            "77-78,5-94\n" +
            "9-11,10-11\n" +
            "51-69,51-68\n" +
            "34-73,46-52\n" +
            "25-96,96-98\n" +
            "1-55,2-55\n" +
            "20-81,26-70\n" +
            "91-97,79-92\n" +
            "22-74,9-22\n" +
            "3-97,2-3\n" +
            "39-94,32-93\n" +
            "66-95,96-97\n" +
            "10-10,9-11\n" +
            "30-65,21-40\n" +
            "2-94,4-93\n" +
            "1-91,2-90\n" +
            "31-84,84-99\n" +
            "82-95,6-75\n" +
            "67-91,90-91\n" +
            "12-12,12-97\n" +
            "7-75,6-7\n" +
            "28-28,14-27\n" +
            "45-50,35-49\n" +
            "43-72,71-73\n" +
            "59-73,21-73\n" +
            "58-75,39-82\n" +
            "96-97,96-98\n" +
            "97-97,2-98\n" +
            "6-92,3-7\n" +
            "84-86,27-85\n" +
            "39-95,31-45\n" +
            "27-84,43-95\n" +
            "48-95,33-95\n" +
            "47-55,46-48\n" +
            "42-82,41-69\n" +
            "12-96,13-98\n" +
            "6-99,1-4\n" +
            "62-94,62-94\n" +
            "64-82,4-83\n" +
            "73-75,10-74\n" +
            "11-24,10-12\n" +
            "23-54,7-23\n" +
            "48-53,18-69\n" +
            "7-11,10-11\n" +
            "87-87,11-88\n" +
            "50-79,78-78\n" +
            "4-52,51-53\n" +
            "81-82,39-82\n" +
            "94-97,1-95\n" +
            "15-75,33-75\n" +
            "40-52,33-38\n" +
            "34-71,70-71\n" +
            "37-38,38-66\n" +
            "7-43,5-6\n" +
            "11-20,10-31\n" +
            "19-98,1-4\n" +
            "19-87,18-88\n" +
            "94-95,18-95\n" +
            "5-13,7-98\n" +
            "45-73,32-45\n" +
            "18-95,94-94\n" +
            "37-95,94-95\n" +
            "41-42,26-43\n" +
            "18-92,19-92\n" +
            "70-90,19-79\n" +
            "16-95,15-96";

    private static final String TST_INPUT = "2-4,6-8\n" +
            "2-3,4-5\n" +
            "5-7,7-9\n" +
            "2-8,3-7\n" +
            "6-6,4-6\n" +
            "2-6,4-8";
}
