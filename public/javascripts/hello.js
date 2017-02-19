$(document).ready(function() {
    $("#btn").click(function() {
      $.ajax("test", {
          success: function (data) {
              console.log(data)
              var arrdata = JSON.parse(data)
              var container = document.getElementById('myGrid');
                var hot = new Handsontable(container, {
                  data: arrdata,
                  rowHeaders: false,
                  colHeaders: data[0]
                });
          }
      })
    })

var container = document.getElementById('myGrid');

function createTableElements(targetel, date, callback){
    var contnr = document.createElement("DIV")
    contnr.setAttribute("id", date)
    targetel.append(contnr)

    for(i=0; i<3; i++){
        var elem = document.createElement("DIV")
        elem.setAttribute("id", date.concat("-", i+1));
        contnr.append(elem)
    }

    callback(date);
}

createTableElements(container, "2017/02/11", function(date) {

    for(i=0;i<3;i++) {
        var target = document.getElementById(date.concat("-", i+1));

      $.ajax("test", {
          success: function (data) {
              console.log(data)
              var arrdata = JSON.parse(data)
              var container = document.getElementById('myGrid');
                var hot = new Handsontable(target, {
                  data: arrdata,
                  rowHeaders: false,
                  colHeaders: data[0]
                });
          }
      })
    }
})
createTableElements(container, "2017/02/12", function(date) {

    var testdata = [
    ["", "Ford", "Volvo", "Toyota", "Honda"],
    ["2016", 10, 11, 12, 13],
    ["2017", 20, 11, 14, 13],
    ["2018", 30, 15, 12, 13],
    ["2019", 30, 15, 12, 13]
    ];
    console.log (JSON.stringify(testdata))

    for(i=0;i<3;i++) {
        var target = document.getElementById(date.concat("-", i+1));
        var hot = new Handsontable(target, {
          data: JSON.parse(JSON.stringify(testdata)),
          rowHeaders: false,
          startRows: 10,
          colHeaders: testdata[0]
        });
    }
})

})
