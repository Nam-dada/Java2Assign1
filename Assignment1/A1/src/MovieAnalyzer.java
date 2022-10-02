import java.util.*;
import java.io.*;
import java.util.stream.Collectors;


public class MovieAnalyzer {

    public class Movie {
        private final String Series_Title;
        private final String Certificate;
        private final String Runtime;
        private final String Genre;
        private final String Overview;
        private final String Director;
        private final String Star1;
        private final String Star2;
        private final String Star3;
        private final String Star4;

        private final int Released_Year;
        private final String Meta_score;
        private final String Noofvotes;
        private final String Gross;
        private final double IMDB_Rating;

        public Movie(String series_Title, String released_Year, String certificate, String runtime,
                     String genre, String IMDB_Rating, String overview, String meta_score, String director,
                     String star1, String star2, String star3, String star4, String noofvotes, String gross
        ) {
            Series_Title = series_Title;
            Certificate = certificate;
            Runtime = runtime;
            Genre = genre;
            Overview = overview;
            Director = director;
            Star1 = star1;
            Star2 = star2;
            Star3 = star3;
            Star4 = star4;
            Released_Year = Integer.parseInt(released_Year);
            Meta_score = meta_score;
            Noofvotes = noofvotes;
            Gross = gross;
            this.IMDB_Rating = Double.parseDouble(IMDB_Rating);
        }

        public String getSeries_Title() {
            return Series_Title;
        }

        public String getCertificate() {
            return Certificate;
        }

        public String getRuntime() {
            return Runtime;
        }

        public int getRuntimeNum() {
            return Integer.parseInt(Runtime.replace(" min", ""));
        }

        public String getGenre() {
            return Genre;
        }

        public String getOverview() {
            return Overview;
        }

        public int getOverviewLength() {
            return Overview.length();
        }

        public String getDirector() {
            return Director;
        }

        public String getStar1() {
            return Star1;
        }

        public String getStar2() {
            return Star2;
        }

        public String getStar3() {
            return Star3;
        }

        public String getStar4() {
            return Star4;
        }

        public int getReleased_Year() {
            return Released_Year;
        }

        public String getMeta_score() {
            return Meta_score;
        }

        public String getNoofvotes() {
            return Noofvotes;
        }

        public String getGross() {
            return Gross;
        }

        public double getGross_double() {
            if (!Gross.isEmpty()) return Double.parseDouble(Gross);
            else return 0;
        }

        public double getIMDB_Rating() {
            return IMDB_Rating;
        }
    }

    static List<Movie> movies = new ArrayList<>();

    public MovieAnalyzer(String dataset_path) {

        try (BufferedReader infile
                     = new BufferedReader(new FileReader(dataset_path))) {
            String line = infile.readLine();

//            while ((line = infile.readLine()) != null) {
//                String[] a = dealString(line);
//                movies.add(new Movie(a[1], a[2], a[3], a[4], a[5],
//                        a[6], a[7], a[8], a[9], a[10],
//                        a[11], a[12], a[13], a[14], a[15]));
//            }
            movies = infile.lines()
                    .map(this::dealString)
                    .map(a -> new Movie(a[1], a[2], a[3], a[4], a[5],
                            a[6], a[7], a[8], a[9], a[10],
                            a[11], a[12], a[13], a[14], a[15].replace(",","")))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String[] dealString(String info) {
        String[] repo = new String[16];
        char[] temp = info.concat(",").toCharArray();
        int id = 0, l = 0;
        boolean flag = false;
        int cnt = 0;
        for (int i = 0; i < temp.length; i++) {
            if (temp[i] == '\"') {
                cnt++;
                if ((i + 1) < temp.length && temp[i + 1] == '\"') continue;
                if ((i - 1) > -1 && temp[i - 1] == '\"') {
                    cnt -= 2;
                    if (cnt % 2 == 1) continue;
                }

                if (flag) {
                    repo[id] = info.substring(l, i);
                    ++id;
                    ++i;
                }
                flag = !flag;
                l = i + 1;
                continue;
            }
            if (temp[i] == ',' && !flag) {
                repo[id] = info.substring(l, i);
                ++id;
                l = i + 1;
            }
        }
        return repo;
    }

    public static Map<Integer, Integer> getMovieCountByYear() {
//        Map<Integer, Integer> temp = movies.stream().collect(Collectors.groupingBy(
//                Movie::getReleased_Year,
//                () -> new TreeMap<>(Comparator.reverseOrder()),
//                Collectors.reducing(0, movie->1, Integer::sum)
//        ));
        return movies.stream().collect(Collectors.groupingBy(
                Movie::getReleased_Year,
                TreeMap::new,
                Collectors.reducing(0, movie -> 1, Integer::sum))).descendingMap();
    }

    public static Map<String, Integer> getMovieCountByGenre() {
        Map<String, Integer> temp = new TreeMap<>();
        movies.forEach(movie -> {
            String[] t1 = movie.getGenre().split(", ");
            for (String i : t1){
                if (temp.containsKey(i)) temp.put(i, temp.get(i)+1);
                else temp.put(i, 1);
            }
        });
        Map<String, Integer> ans = new LinkedHashMap<>();
        temp.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> ans.put(x.getKey(), x.getValue()));
        return ans;
    }

    public static Map<List<String>, Integer> getCoStarCount() {
        Map<List<String>, Integer> temp =  movies.stream().collect(Collectors.groupingBy(
                movie -> new ArrayList<>(Arrays.asList(movie.getStar1(), movie.getStar2())).stream()
                        .sorted(Comparator.naturalOrder()).collect(Collectors.toList()),
                Collectors.reducing(0, movie -> 1, Integer::sum)
        ));
        List<List<String>> t1 = movies.stream().map(movie -> new ArrayList<>(Arrays.asList(movie.getStar1(), movie.getStar3())).stream()
                .sorted(Comparator.naturalOrder()).collect(Collectors.toList())).collect(Collectors.toList());
        List<List<String>> t2 = movies.stream().map(movie -> new ArrayList<>(Arrays.asList(movie.getStar1(), movie.getStar4())).stream()
                .sorted(Comparator.naturalOrder()).collect(Collectors.toList())).collect(Collectors.toList());
        List<List<String>> t3 = movies.stream().map(movie -> new ArrayList<>(Arrays.asList(movie.getStar2(), movie.getStar3())).stream()
                .sorted(Comparator.naturalOrder()).collect(Collectors.toList())).collect(Collectors.toList());
        List<List<String>> t4 = movies.stream().map(movie -> new ArrayList<>(Arrays.asList(movie.getStar2(), movie.getStar4())).stream()
                .sorted(Comparator.naturalOrder()).collect(Collectors.toList())).collect(Collectors.toList());
        List<List<String>> t5 = movies.stream().map(movie -> new ArrayList<>(Arrays.asList(movie.getStar3(), movie.getStar4())).stream()
                .sorted(Comparator.naturalOrder()).collect(Collectors.toList())).collect(Collectors.toList());

        final boolean[] isExisted = {false};
        t1.forEach(z -> {
            isExisted[0] = false;
            temp.forEach((x,y) -> {
                String x0 = x.get(0);
                String x1 = x.get(1);
                String z0 = z.get(0);
                String z1 = z.get(1);
                if ((z0.equals(x0) && z1.equals(x1))) {
                    temp.put(x,y+1);
                    isExisted[0] = true;
                }
            });
            if (!isExisted[0]) temp.put(z,1);
        });

        t2.forEach(z -> {
            isExisted[0] = false;
            temp.forEach((x,y) -> {
                String x0 = x.get(0);
                String x1 = x.get(1);
                String z0 = z.get(0);
                String z1 = z.get(1);
                if ((z0.equals(x0) && z1.equals(x1))) {
                    temp.put(x,y+1);
                    isExisted[0] = true;
                }
            });
            if (!isExisted[0]) temp.put(z,1);
        });

        t3.forEach(z -> {
            isExisted[0] = false;
            temp.forEach((x,y) -> {
                String x0 = x.get(0);
                String x1 = x.get(1);
                String z0 = z.get(0);
                String z1 = z.get(1);
                if ((z0.equals(x0) && z1.equals(x1))) {
                    temp.put(x,y+1);
                    isExisted[0] = true;
                }
            });
            if (!isExisted[0]) temp.put(z,1);
        });

        t4.forEach(z -> {
            isExisted[0] = false;
            temp.forEach((x,y) -> {
                String x0 = x.get(0);
                String x1 = x.get(1);
                String z0 = z.get(0);
                String z1 = z.get(1);
                if ((z0.equals(x0) && z1.equals(x1))) {
                    temp.put(x,y+1);
                    isExisted[0] = true;
                }
            });
            if (!isExisted[0]) temp.put(z,1);
        });

        t5.forEach(z -> {
            isExisted[0] = false;
            temp.forEach((x,y) -> {
                String x0 = x.get(0);
                String x1 = x.get(1);
                String z0 = z.get(0);
                String z1 = z.get(1);
                if ((z0.equals(x0) && z1.equals(x1))) {
                    temp.put(x,y+1);
                    isExisted[0] = true;
                }
            });
            if (!isExisted[0]) temp.put(z,1);
        });

        Map<List<String>, Integer> ans = new LinkedHashMap<>();
        temp.entrySet().stream().sorted((x,y) -> y.getValue() - x.getValue()).forEachOrdered(e -> ans.put(e.getKey(), e.getValue()));
        return ans;
    }

    public static List<String> getTopMovies(int top_k, String by){
        if (by.equals("runtime")){
            return movies.stream()
                    .sorted(Comparator.comparing(Movie::getRuntimeNum, Comparator.reverseOrder()).thenComparing(Movie::getSeries_Title))
                    .limit(top_k)
                    .map(Movie::getSeries_Title).collect(Collectors.toList());
        }else {  //by == "overview"
            return movies.stream()
                    .sorted(Comparator.comparing(Movie::getOverviewLength, Comparator.reverseOrder()).thenComparing(Movie::getSeries_Title))
                    .limit(top_k)
                    .map(Movie::getSeries_Title).collect(Collectors.toList());
        }
    }

    public static List<String> getTopStars(int top_k, String by){
        if (by.equals("rating")){
            Map<String, List<Movie>> s1 = movies.stream().collect(Collectors.groupingBy(Movie::getStar1));
            Map<String, List<Movie>> s2 = movies.stream().collect(Collectors.groupingBy(Movie::getStar2));
            Map<String, List<Movie>> s3 = movies.stream().collect(Collectors.groupingBy(Movie::getStar3));
            Map<String, List<Movie>> s4 = movies.stream().collect(Collectors.groupingBy(Movie::getStar4));
            s2.forEach(
                    (x,y) -> {
                        List<Movie> t1 = s1.get(x);
                        if (t1!=null) {
                            y.forEach(z -> {if (!t1.contains(z)) t1.add(z);});
                            s1.put(x,t1);
                        }
                        else s1.put(x,y);
                    }
            );
            s3.forEach(
                    (x,y) -> {
                        List<Movie> t1 = s1.get(x);
                        if (t1!=null) {
                            y.forEach(z -> {if (!t1.contains(z)) t1.add(z);});
                            s1.put(x,t1);
                        }
                        else s1.put(x,y);
                    }
            );
            s4.forEach(
                    (x,y) -> {
                        List<Movie> t1 = s1.get(x);
                        if (t1!=null) {
                            y.forEach(z -> {if (!t1.contains(z)) t1.add(z);});
                            s1.put(x,t1);
                        }
                        else s1.put(x,y);
                    }
            );
            Map<String, Double> avgRate = new TreeMap<>();
            s1.forEach((k,v)->{
                avgRate.put(k, v.stream().mapToDouble(Movie::getIMDB_Rating).sum()/v.size());
            });

            Map<String, Double> ans = new LinkedHashMap<>();
            avgRate.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .limit(top_k)
                    .forEachOrdered(
                            x -> ans.put(x.getKey(), x.getValue())
                    );
            List<String> pp = new ArrayList<>();
            ans.forEach((x,y) -> pp.add(x));

            return pp;
        }else {  //by == "gross"
            Map<String, List<Movie>> s1 = movies.stream().filter(movie -> !movie.getGross().isEmpty())
                    .collect(Collectors.groupingBy(Movie::getStar1));
            Map<String, List<Movie>> s2 = movies.stream().filter(movie -> !movie.getGross().isEmpty())
                    .collect(Collectors.groupingBy(Movie::getStar2));
            Map<String, List<Movie>> s3 = movies.stream().filter(movie -> !movie.getGross().isEmpty())
                    .collect(Collectors.groupingBy(Movie::getStar3));
            Map<String, List<Movie>> s4 = movies.stream().filter(movie -> !movie.getGross().isEmpty())
                    .collect(Collectors.groupingBy(Movie::getStar4));
            s2.forEach(
                    (x,y) -> {
                        List<Movie> t1 = s1.get(x);
                        if (t1!=null) {
                            y.forEach(z -> {if (!t1.contains(z)) t1.add(z);});
                            s1.put(x,t1);
                        }
                        else s1.put(x,y);
                    }
            );
            s3.forEach(
                    (x,y) -> {
                        List<Movie> t1 = s1.get(x);
                        if (t1!=null) {
                            y.forEach(z -> {if (!t1.contains(z)) t1.add(z);});
                            s1.put(x,t1);
                        }
                        else s1.put(x,y);
                    }
            );
            s4.forEach(
                    (x,y) -> {
                        List<Movie> t1 = s1.get(x);
                        if (t1!=null) {
                            y.forEach(z -> {if (!t1.contains(z)) t1.add(z);});
                            s1.put(x,t1);
                        }
                        else s1.put(x,y);
                    }
            );
            Map<String, Double> avgRate = new TreeMap<>();
            s1.forEach((k,v)->{
                avgRate.put(k, v.stream().mapToDouble(Movie::getGross_double).sum()/v.size());
            });

            Map<String, Double> ans = new LinkedHashMap<>();
            avgRate.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .limit(top_k)
                    .forEachOrdered(
                            x -> ans.put(x.getKey(), x.getValue())
                    );
            List<String> pp = new ArrayList<>();
            ans.forEach((x,y) -> pp.add(x));

            return pp;
        }
    }

    public static List<String> searchMovies(String genre, float min_rating, int max_runtime){
        return movies.stream()
                .filter(movie -> movie.getGenre().equals(genre))
                .filter(movie -> movie.getIMDB_Rating() >= min_rating)
                .filter(movie -> Integer.parseInt(movie.getRuntime().replace(" min","")) <= max_runtime)
                .map(Movie::getSeries_Title)
                .collect(Collectors.toList());
    }

    public static void main(String[] args) {
        MovieAnalyzer movieAnalyzer = new MovieAnalyzer("resources/imdb_top_500.csv");
        System.out.println(getTopStars(15, "g"));
    }

}