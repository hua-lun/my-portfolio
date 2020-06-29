google.charts.load('current', {'packages':['corechart']});
google.charts.setOnLoadCallback(drawColumnChart);

/** Fetches kpop idols by birth year data and uses it to create a chart. */
function drawColumnChart() {
  fetch('/kpopidols-data').then(response => response.json())
  .then((kpopIdolsByYear) => {
    const data = new google.visualization.DataTable();
    data.addColumn('string', 'Year');
    data.addColumn('number', 'Frequencies');
    Object.keys(kpopIdolsByYear).forEach((year) => {
      data.addRow([year, kpopIdolsByYear[year]]);
    });

    const options = {
      'title': 'Kpop Idols By Birth Year',
      'width':800,
      'height':700,
      backgroundColor: '#73C6B6'
    };

    const chart = new google.visualization.ColumnChart(
        document.getElementById('chart-kpopidols'));
    chart.draw(data, options);
  });
}
