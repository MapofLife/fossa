(ns fossa.core-test
  (:use [midje sweet cascalog]
        fossa.core))

(fact
  "Test read-occurrences using sample dataset"
  (read-occurrences)
  => (produces-some [["Acidobacteria" (float 41.526566) (float -70.67076) "242135147" "" "2007" "1"]]))

(fact
  "Test parse-occurrence-data"
  (parse-occurrence-data)
  => (produces [["Acidobacteria" "INSERT INTO gbif_points (name, occids, precision, year, month, season, the_geom_multipoint) values ('Acidobacteria', '{\"244664083\", \"244662763\", \"100000000,500000000\", \"244662123\", \"242136127\", \"242135147\", \"242135541\", \"244666043\", \"244661001\", \"244661391\", \"242135095\"}', '{\"\", \"\", \",\", \"\", \"\", \"\", \"\", \"\", \"\", \"\", \"\"}', '{\"2007\", \"2007\", \"2008,2010\", \"2008\", \"2007\", \"2007\", \"2007\", \"1992\", \"2008\", \"2007\", \"2005\"}', '{\"6\", \"4\", \"10,3\", \"9\", \"3\", \"1\", \"6\", \"4\", \"5\", \"8\", \"3\"}', '{\"S winter\", \"S fall\", \"S spring,S fall\", \"N fall\", \"N spring\", \"N winter\", \"N summer\", \"N spring\", \"N summer\", \"N fall\", \"N spring\"}' ST_GeomFromText('MULTIPOINT (170.851 -40.8747, -175.5532 -43.288, 0.0 0.0, 73.88306 15.509722, 35.416668 31.416666, -70.67076 41.526566, -70.6336 41.576233, 19.583334 47.166668, 6.144433 53.49027, -140.088 73.967, 0.89666665 9.931666)', 4326))"]]))
