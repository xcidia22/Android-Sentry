var db = firebase.firestore();
var docRef = db.collection("Officers");
var docRef2 = db.collection("ClosedIncidents");

var userMobile = '123';
var selectedMobile = '123';
var validMobile = false;
var validName = false;
var numTest = '';
var numTestLength = 0;
var existingMobile = false;


function storeData(){
    
    var inputMobile = document.getElementById("mobile_field").value;
    var inputMobileX = '+63' + inputMobile;
    var inputEmail = document.getElementById("email_field").value; 
    
    var inputFirst = document.getElementById("first_field").value; 
    var inputLast = document.getElementById("last_field").value; 
    
    var inputName = inputFirst + ' ' + inputLast;
    
    
    var rank = document.getElementById("rank_field");
    var inputRank = rank.options[rank.selectedIndex].text;
    
    checkMobile();
    checkName();
    
    docRef.get().then((snapshot) => {
        snapshot.forEach(function(docs){
            userMobile = docs.data().mobile;
            
            if(userMobile === inputMobileX){
                existingMobile = true;
            }
            else {
                existingMobile = false;
            }
        });
    });
    
    
    if (existingMobile == false){
        if (validName === true && validMobile === true){
            docRef.doc().set({
            mobile: '+63' + inputMobile,
            rank: inputRank,
            name: inputName,
            email: inputEmail,
            Latitude: 0.0,
            Longitude: 0.0,
            hidden: false
            })

            $("#addModal").modal("show");
        }
    }
    else {
        alert("Mobile number already exist");
    }
    
}

function updateData(){
    
    var inputMobile = document.getElementById("mobile_field").value;  
    var inputEmail = document.getElementById("email_field").value; 
    
    var inputFirst = document.getElementById("first_field").value; 
    var inputLast = document.getElementById("last_field").value; 
    
    var inputName = inputFirst + ' ' + inputLast;

    var rank = document.getElementById("rank_field");
    var inputRank = rank.options[rank.selectedIndex].text;

    checkName();

    if (validName === true){
        
        docRef.get().then((snapshot) => {
        snapshot.forEach(function(docs){
            var docId = docs.id.toString();
            userMobile = docs.data().mobile;

                if(userMobile === inputMobile ){
                    docRef.doc(docId).update({
                    "rank": inputRank,
                    "name": inputName,
                    "email": inputEmail
                    })

                    $("#updateModal").modal("show");      
                }
        });
        });
   }
} 

function deleteData(){

        docRef.get().then((snapshot) => {
        snapshot.forEach(function(docs){
            var docId = docs.id.toString();
            userMobile = docs.data().mobile;
            
            if(userMobile === selectedMobile ){
                docRef.doc(docId).delete();
                alert("Record deleted");
                
                $("#deleteModal").modal("hide");
            }
        });

        });
}

const list_tbody = document.querySelector("#list_tbody");
db.collection("Officers").onSnapshot(function(querySnapshot) {
    
    querySnapshot.docChanges().forEach(function(change){
        
        if (change.type == "added"){
        
            list_tbody.innerHTML += "<tr><td class='numberclass'>" + change.doc.data().mobile + "</td><td>" + change.doc.data().name + "</td><td>" + change.doc.data().rank + "</td><td>" + change.doc.data().email + "</td>" + "<td class = 'text-center'><button type = 'button' class='btnUpdate'>Update</button></td>" + "<td class = 'text-center'><button type = 'button' class='btnDel'>Delete</button></td></tr>"
        }
        
    });

    
    $(".btnDel").click(function(){
        selectedMobile = $(this).closest("tr").find(".numberclass").text();
        $("#deleteModal").modal("show");

    });  
    
    $(".btnUpdate").click(function(){
        selectedMobile = $(this).closest("tr").find(".numberclass").text();
        setNumber();
        document.location = 'update.html';
        
    });  
});

const review_tbody = document.querySelector("#review_tbody");
db.collection("Reviews").onSnapshot(function(querySnapshot) {
    
    querySnapshot.docChanges().forEach(function(change){
        
        if (change.type == "added"){
            review_tbody.innerHTML += "<tr><td>" + change.doc.data().email + "</td><td>" + change.doc.data().Rating + "</td><td>" + change.doc.data().Review + "</td></tr>"
        }
        
    });
});

function onLoadIndex(){
    countOfficer();
    countIncidents();
    countUsers();

    
}

function countOfficer(){
 
var officerCounter = 0;

    docRef.get().then((snapshot) => {
        snapshot.forEach(function(docs){
            officerCounter++;
        }); 
        document.getElementById("officerCount").innerHTML = officerCounter + " Registered Officers";
    });   
    
} 

function countIncidents(){
 
var incidentCounter = 0;

    docRef2.get().then((snapshot) => {
        snapshot.forEach(function(docs){
            incidentCounter++;
        }); 
        document.getElementById("incidentCount").innerHTML = incidentCounter + " Total Reported Incidents";
    });   
    
} 

function countUsers(){
 
var userCounter = 8;


    document.getElementById("userCount").innerHTML = userCounter + " Total Users";
 
    
} 



function setNumber(){
    localStorage.setItem("storageMobile",selectedMobile);
}  

function checkName(){    
    var nameTest = /^[A-Za-z ]+$/;

    if(nameTest.test(document.getElementById("first_field").value) && nameTest.test(document.getElementById("last_field").value)){
        validName = true;
    }
       
    else{
        alert('Enter valid name'); 
        validName = false;
    }   
}

function checkMobile(){
    numTest = document.getElementById("mobile_field").value;
    numTestLength = numTest.length;
    var regex=/^[0-9]+$/;
    
    if (numTestLength > 0 && numTestLength < 11){
        if (!numTest.match(regex)){
            validMobile = false;
            alert("Enter number only in number field");
        }
        else {
            validMobile = true;
        }
    }
    else {
        validMobile = false;
        alert("Invalid length in number field");
    }
}

function onLoadIncidents(){
    createPie();
    createBar();
    createIncidentTable();
}

function createPie(){
 
var violenceCounter = 0;
var propertyCounter = 0;
var disturbanceCounter = 0;
      
var month = document.getElementById("month_field");
var inputMonth = month.options[month.selectedIndex].text;
    
    
    if (inputMonth === 'January 2019'){
        docRef2.get().then((snapshot) => {
        snapshot.forEach(function(docs){
            var category = docs.data().Category;  
            var time = docs.data().Timestamp;
            var timeStamp = time.substring(0, 2);

            if (timeStamp == '01'){
                if(category === 'Violence'){
                    violenceCounter++;
                }
                else if(category === 'Property'){
                    propertyCounter++;
                }
                else if(category === 'Community Disturbance'){
                    disturbanceCounter++;
                }
                
                
                Chart.defaults.global.defaultFontFamily = '-apple-system,system-ui,BlinkMacSystemFont,"Segoe UI",Roboto,"Helvetica Neue",Arial,sans-serif';
                Chart.defaults.global.defaultFontColor = '#292b2c';

                // Pie Chart Example
                var ctx = document.getElementById("myPieChart");
                
                var myPieChart = new Chart(ctx, {
                  type: 'pie',
                  data: {
                    labels: ["Violence", "Property", "Disturbance"],
                    datasets: [{
                      data: [violenceCounter, propertyCounter, disturbanceCounter],
                      backgroundColor: ['#007bff', '#dc3545', '#ffc107'],
                    }],
                  },
                });
            }
        });   
      });   
    } 
    
    
    else if (inputMonth === 'February 2019'){
        docRef2.get().then((snapshot) => {
        snapshot.forEach(function(docs){
            var category = docs.data().Category;  
            var time = docs.data().Timestamp;
            var timeStamp = time.substring(0, 2);

            if (timeStamp == '02'){
                if(category === 'Violence'){
                    violenceCounter++;
                }
                else if(category === 'Property'){
                    propertyCounter++;
                }
                else if(category === 'Community Disturbance'){
                    disturbanceCounter++;
                }
                
                
                Chart.defaults.global.defaultFontFamily = '-apple-system,system-ui,BlinkMacSystemFont,"Segoe UI",Roboto,"Helvetica Neue",Arial,sans-serif';
                Chart.defaults.global.defaultFontColor = '#292b2c';

                // Pie Chart Example
                var ctx = document.getElementById("myPieChart");
                
                var myPieChart = new Chart(ctx, {
                  type: 'pie',
                  data: {
                    labels: ["Violence", "Property", "Disturbance"],
                    datasets: [{
                      data: [violenceCounter, propertyCounter, disturbanceCounter],
                      backgroundColor: ['#007bff', '#dc3545', '#ffc107'],
                    }],
                  },
                });
            }
        });   
      });   
    } 
    
    else if (inputMonth === 'March 2019'){
        docRef2.get().then((snapshot) => {
        snapshot.forEach(function(docs){
            var category = docs.data().Category;  
            var time = docs.data().Timestamp;
            var timeStamp = time.substring(0, 2);

            if (timeStamp == '03'){
                if(category === 'Violence'){
                    violenceCounter++;
                }
                else if(category === 'Property'){
                    propertyCounter++;
                }
                else if(category === 'Disturbance'){
                    disturbanceCounter++;
                }
                
                
                Chart.defaults.global.defaultFontFamily = '-apple-system,system-ui,BlinkMacSystemFont,"Segoe UI",Roboto,"Helvetica Neue",Arial,sans-serif';
                Chart.defaults.global.defaultFontColor = '#292b2c';

                // Pie Chart Example
                var ctx = document.getElementById("myPieChart");
                
                var myPieChart = new Chart(ctx, {
                  type: 'pie',
                  data: {
                    labels: ["Violence", "Property", "Disturbance"],
                    datasets: [{
                      data: [violenceCounter, propertyCounter, disturbanceCounter],
                      backgroundColor: ['#007bff', '#dc3545', '#ffc107'],
                    }],
                  },
                });
            }
            
            else {
                Chart.defaults.global.defaultFontFamily = '-apple-system,system-ui,BlinkMacSystemFont,"Segoe UI",Roboto,"Helvetica Neue",Arial,sans-serif';
                Chart.defaults.global.defaultFontColor = '#292b2c';

                // Pie Chart Example
                var ctx = document.getElementById("myPieChart");
                
                var myPieChart = new Chart(ctx, {
                  type: 'pie',
                  data: {
                    labels: ["Violence", "Property", "Disturbance"],
                    datasets: [{
                      data: [violenceCounter, propertyCounter, disturbanceCounter],
                      backgroundColor: ['#007bff', '#dc3545', '#ffc107'],
                    }],
                  },
                });
            }
        });   
      });   
    } 

    
    
} 

function createBar(){
    
var OutsideBacolodCounter = 0;
var Barangay33Counter = 0;
var unorCounter = 0;
var Barangay36Counter = 0;
    
var month = document.getElementById("month_field");
var inputMonth = month.options[month.selectedIndex].text;
    
    
    if (inputMonth === 'January 2019'){
        docRef2.get().then((snapshot) => {
        snapshot.forEach(function(docs){
            var origin = docs.data().IncidentOrigin;  
            var time = docs.data().Timestamp;
            var timeStamp = time.substring(0, 2);

            if (timeStamp == '01'){
                if(origin === 'OutsideBacolodArea'){
                    OutsideBacolodCounter++;
                }
                else if(origin === 'Barangay33'){
                    Barangay33Counter++;
                }
                else if(origin === 'UNOR Area'){
                    unorCounter++;
                }
                else if(origin === 'Barangay36'){
                    Barangay36Counter++;
                }
            }
        });
            
            
            Chart.defaults.global.defaultFontFamily = '-apple-system,system-ui,BlinkMacSystemFont,"Segoe UI",Roboto,"Helvetica Neue",Arial,sans-serif';
            Chart.defaults.global.defaultFontColor = '#292b2c';

            // Bar Chart Example
            var ctx = document.getElementById("myBarChart");
            var myLineChart = new Chart(ctx, {
              type: 'bar',
              data: {
                labels: ["Barangay 1", "Barangay 2", "Barangay 36", "Outside Bacolod Area", "Barangay 33", "UNOR Area"],
                datasets: [{
                  label: "Incidents",
                  backgroundColor: "rgba(2,117,216,1)",
                  borderColor: "rgba(2,117,216,1)",
                  data: [0, 0, Barangay36Counter, OutsideBacolodCounter, Barangay33Counter, unorCounter],
                }],
              },
              options: {
                scales: {
                  xAxes: [{
                    time: {
                      unit: 'month'
                    },
                    gridLines: {
                      display: false
                    },
                    ticks: {
                      maxTicksLimit: 6
                    }
                  }],
                  yAxes: [{
                    ticks: {
                      min: 0,
                      max: 40,
                      maxTicksLimit: 5
                    },
                    gridLines: {
                      display: true
                    }
                  }],
                },
                legend: {
                  display: false
                }
              }
            });  
      });   
    } 
    
    else if (inputMonth === 'February 2019'){
        docRef2.get().then((snapshot) => {
        snapshot.forEach(function(docs){
            var origin = docs.data().IncidentOrigin;  
            var time = docs.data().Timestamp;
            var timeStamp = time.substring(0, 2);

            if (timeStamp == '02'){
                if(origin === 'OutsideBacolodArea'){
                    OutsideBacolodCounter++;
                }
                else if(origin === 'Barangay33'){
                    Barangay33Counter++;
                }
                else if(origin === 'UNOR Area'){
                    unorCounter++;
                }
                else if(origin === 'Barangay36'){
                    Barangay36Counter++;
                }
            }
        });
            
            
            Chart.defaults.global.defaultFontFamily = '-apple-system,system-ui,BlinkMacSystemFont,"Segoe UI",Roboto,"Helvetica Neue",Arial,sans-serif';
            Chart.defaults.global.defaultFontColor = '#292b2c';

            // Bar Chart Example
            var ctx = document.getElementById("myBarChart");
            var myLineChart = new Chart(ctx, {
              type: 'bar',
              data: {
                labels: ["Barangay 1", "Barangay 2", "Barangay 3", "Outside Bacolod Area", "Barangay 33", "UNOR Area"],
                datasets: [{
                  label: "Incidents",
                  backgroundColor: "rgba(2,117,216,1)",
                  borderColor: "rgba(2,117,216,1)",
                  data: [0, 0, 0, OutsideBacolodCounter, Barangay33Counter, unorCounter],
                }],
              },
              options: {
                scales: {
                  xAxes: [{
                    time: {
                      unit: 'month'
                    },
                    gridLines: {
                      display: false
                    },
                    ticks: {
                      maxTicksLimit: 6
                    }
                  }],
                  yAxes: [{
                    ticks: {
                      min: 0,
                      max: 40,
                      maxTicksLimit: 5
                    },
                    gridLines: {
                      display: true
                    }
                  }],
                },
                legend: {
                  display: false
                }
              }
            });  
      });   
    } 
    
    else if (inputMonth === 'March 2019'){
        docRef2.get().then((snapshot) => {
        snapshot.forEach(function(docs){
            var origin = docs.data().IncidentOrigin;  
            var time = docs.data().Timestamp;
            var timeStamp = time.substring(0, 2);

            if (timeStamp == '03'){
                if(origin === 'OutsideBacolodArea'){
                    OutsideBacolodCounter++;
                }
                else if(origin === 'Barangay33'){
                    Barangay33Counter++;
                }
                else if(origin === 'UNOR Area'){
                    unorCounter++;
                }
                else if(origin === 'Barangay36'){
                    Barangay36Counter++;
                }
            }
        });
            
            
            Chart.defaults.global.defaultFontFamily = '-apple-system,system-ui,BlinkMacSystemFont,"Segoe UI",Roboto,"Helvetica Neue",Arial,sans-serif';
            Chart.defaults.global.defaultFontColor = '#292b2c';

            // Bar Chart Example
            var ctx = document.getElementById("myBarChart");
            var myLineChart = new Chart(ctx, {
              type: 'bar',
              data: {
                labels: ["Barangay 1", "Barangay 2", "Barangay 3", "Outside Bacolod Area", "Barangay 33", "UNOR Area"],
                datasets: [{
                  label: "Incidents",
                  backgroundColor: "rgba(2,117,216,1)",
                  borderColor: "rgba(2,117,216,1)",
                  data: [0, 0, 0, OutsideBacolodCounter, Barangay33Counter, unorCounter],
                }],
              },
              options: {
                scales: {
                  xAxes: [{
                    time: {
                      unit: 'month'
                    },
                    gridLines: {
                      display: false
                    },
                    ticks: {
                      maxTicksLimit: 6
                    }
                  }],
                  yAxes: [{
                    ticks: {
                      min: 0,
                      max: 40,
                      maxTicksLimit: 5
                    },
                    gridLines: {
                      display: true
                    }
                  }],
                },
                legend: {
                  display: false
                }
              }
            });  
      });   
    } 
    
 
    
    
    
    
}

function createIncidentTable(){
    
    var table = document.getElementById("table_id");
    //or use :  var table = document.all.tableid;
    for(var i = table.rows.length - 1; i > 0; i--)
    {
        table.deleteRow(i);
    }
    
    var month = document.getElementById("month_field");
    var inputMonth = month.options[month.selectedIndex].text;
    
    var category = document.getElementById("category_field");
    var inputCategory = category.options[category.selectedIndex].text;
    
    const incident_tbody = document.querySelector("#incident_tbody");
    
    if (inputMonth === 'January 2019'){
        if (inputCategory === 'Property'){
            db.collection("ClosedIncidents").onSnapshot(function(querySnapshot) {
            querySnapshot.docChanges().forEach(function(change){
                
                var category = change.doc.data().Category;
                var time = change.doc.data().Timestamp;
                var timeStamp = time.substring(0, 2);
                
                if (timeStamp == '01'){
                    if (category === 'Property'){
                        if (change.type == "added"){

                            incident_tbody.innerHTML += "<tr><td>" + change.doc.data().IncidentID + "</td><td>" + change.doc.data().Timestamp + "</td><td>" + change.doc.data().Category + "</td><td>" + change.doc.data().Case + "</td><td>" + change.doc.data().VictimName + "</td><td>" + change.doc.data().IncidentOrigin + "</td><td>" + 
                            "<a href='" + "http://www.google.com/maps/search/?api=1&query=" + change.doc.data().Latitude + "," + change.doc.data().Longitude + "'>Geolocation</a>" + "</td></tr>"
                        }
                    }
                    
                }

            });
            });     
        }
        
        else if (inputCategory === 'Violence'){
            db.collection("ClosedIncidents").onSnapshot(function(querySnapshot) {
            querySnapshot.docChanges().forEach(function(change){
                
                var category = change.doc.data().Category;
                var time = change.doc.data().Timestamp;
                var timeStamp = time.substring(0, 2);
                
                if (timeStamp == '01'){
                    if (category === 'Violence'){
                        if (change.type == "added"){

                            incident_tbody.innerHTML += "<tr><td>" + change.doc.data().IncidentID + "</td><td>" + change.doc.data().Timestamp + "</td><td>" + change.doc.data().Category + "</td><td>" + change.doc.data().Case + "</td><td>" + change.doc.data().VictimName + "</td><td>" + change.doc.data().IncidentOrigin + "</td><td>" + 
                            "<a href='" + "http://www.google.com/maps/search/?api=1&query=" + change.doc.data().Latitude + "," + change.doc.data().Longitude + "'>Geolocation</a>" + "</td></tr>"
                        }
                    }
                    
                }

            });
            });     
        }
        
        else if (inputCategory === 'Community Disturbance'){
            db.collection("ClosedIncidents").onSnapshot(function(querySnapshot) {
            querySnapshot.docChanges().forEach(function(change){
                
                var category = change.doc.data().Category;
                var time = change.doc.data().Timestamp;
                var timeStamp = time.substring(0, 2);
                
                if (timeStamp == '01'){
                    if (category === 'Community Disturbance'){
                        if (change.type == "added"){

                            incident_tbody.innerHTML += "<tr><td>" + change.doc.data().IncidentID + "</td><td>" + change.doc.data().Timestamp + "</td><td>" + change.doc.data().Category + "</td><td>" + change.doc.data().Case + "</td><td>" + change.doc.data().VictimName + "</td><td>" + change.doc.data().IncidentOrigin + "</td><td>" + 
                            "<a href='" + "http://www.google.com/maps/search/?api=1&query=" + change.doc.data().Latitude + "," + change.doc.data().Longitude + "'>Geolocation</a>" + "</td></tr>"
                        }
                    }
                    
                }

            });
            });     
        }
        
        else {
            db.collection("ClosedIncidents").onSnapshot(function(querySnapshot) {
            querySnapshot.docChanges().forEach(function(change){
                
                var time = change.doc.data().Timestamp;
                var timeStamp = time.substring(0, 2);
                
                if (timeStamp == '01'){
                    if (change.type == "added"){

                        incident_tbody.innerHTML += "<tr><td>" + change.doc.data().IncidentID + "</td><td>" + change.doc.data().Timestamp + "</td><td>" + change.doc.data().Category + "</td><td>" + change.doc.data().Case + "</td><td>" + change.doc.data().VictimName + "</td><td>" + change.doc.data().IncidentOrigin + "</td><td>" + 
                            "<a href='" + "http://www.google.com/maps/search/?api=1&query=" + change.doc.data().Latitude + "," + change.doc.data().Longitude + "'>Geolocation</a>" + "</td></tr>"
                    }
                }

            });

        });
        }
    }
    
    
    
    else if (inputMonth === 'February 2019'){
        if (inputCategory === 'Property'){
            db.collection("ClosedIncidents").onSnapshot(function(querySnapshot) {
            querySnapshot.docChanges().forEach(function(change){
                
                var category = change.doc.data().Category;
                var time = change.doc.data().Timestamp;
                var timeStamp = time.substring(0, 2);
                
                if (timeStamp == '02'){
                    if (category === 'Property'){
                        if (change.type == "added"){

                            incident_tbody.innerHTML += "<tr><td>" + change.doc.data().IncidentID + "</td><td>" + change.doc.data().Timestamp + "</td><td>" + change.doc.data().Category + "</td><td>" + change.doc.data().Case + "</td><td>" + change.doc.data().VictimName + "</td><td>" + change.doc.data().IncidentOrigin + "</td><td>" + 
                            "<a href='" + "http://www.google.com/maps/search/?api=1&query=" + change.doc.data().Latitude + "," + change.doc.data().Longitude + "'>Geolocation</a>" + "</td></tr>"
                        }
                    }
                    
                }

            });
            });     
        }
        
        else if (inputCategory === 'Violence'){
            db.collection("ClosedIncidents").onSnapshot(function(querySnapshot) {
            querySnapshot.docChanges().forEach(function(change){
                
                var category = change.doc.data().Category;
                var time = change.doc.data().Timestamp;
                var timeStamp = time.substring(0, 2);
                
                if (timeStamp == '02'){
                    if (category === 'Violence'){
                        if (change.type == "added"){

                            incident_tbody.innerHTML += "<tr><td>" + change.doc.data().IncidentID + "</td><td>" + change.doc.data().Timestamp + "</td><td>" + change.doc.data().Category + "</td><td>" + change.doc.data().Case + "</td><td>" + change.doc.data().VictimName + "</td><td>" + change.doc.data().IncidentOrigin + "</td><td>" + 
                            "<a href='" + "http://www.google.com/maps/search/?api=1&query=" + change.doc.data().Latitude + "," + change.doc.data().Longitude + "'>Geolocation</a>" + "</td></tr>"
                        }
                    }
                    
                }

            });
            });     
        }
        
        else if (inputCategory === 'Community Disturbance'){
            db.collection("ClosedIncidents").onSnapshot(function(querySnapshot) {
            querySnapshot.docChanges().forEach(function(change){
                
                var category = change.doc.data().Category;
                var time = change.doc.data().Timestamp;
                var timeStamp = time.substring(0, 2);
                
                if (timeStamp == '02'){
                    if (category === 'Community Disturbance'){
                        if (change.type == "added"){

                            incident_tbody.innerHTML += "<tr><td>" + change.doc.data().IncidentID + "</td><td>" + change.doc.data().Timestamp + "</td><td>" + change.doc.data().Category + "</td><td>" + change.doc.data().Case + "</td><td>" + change.doc.data().VictimName + "</td><td>" + change.doc.data().IncidentOrigin + "</td><td>" + 
                            "<a href='" + "http://www.google.com/maps/search/?api=1&query=" + change.doc.data().Latitude + "," + change.doc.data().Longitude + "'>Geolocation</a>" + "</td></tr>"
                        }
                    }
                    
                }

            });
            });     
        }
        
        else {
            db.collection("ClosedIncidents").onSnapshot(function(querySnapshot) {
            querySnapshot.docChanges().forEach(function(change){
                
                var time = change.doc.data().Timestamp;
                var timeStamp = time.substring(0, 2);
                
                if (timeStamp == '02'){
                    if (change.type == "added"){

                        incident_tbody.innerHTML += "<tr><td>" + change.doc.data().IncidentID + "</td><td>" + change.doc.data().Timestamp + "</td><td>" + change.doc.data().Category + "</td><td>" + change.doc.data().Case + "</td><td>" + change.doc.data().VictimName + "</td><td>" + change.doc.data().IncidentOrigin + "</td><td>" + 
                            "<a href='" + "http://www.google.com/maps/search/?api=1&query=" + change.doc.data().Latitude + "," + change.doc.data().Longitude + "'>Geolocation</a>" + "</td></tr>"
                    }
                }

            });

        });
        }
//        db.collection("ClosedIncidents").onSnapshot(function(querySnapshot) {
//            querySnapshot.docChanges().forEach(function(change){
//                
//                var time = change.doc.data().Timestamp;
//                var timeStamp = time.substring(0, 2);
//                
//                if (timeStamp == '02'){
//                    if (change.type == "added"){
//
//                        incident_tbody.innerHTML += "<tr><td>" + change.doc.data().IncidentID + "</td><td>" + change.doc.data().Timestamp + "</td><td>" + change.doc.data().Category + "</td><td>" + change.doc.data().Case + "</td><td>" + change.doc.data().IncidentOrigin + "</td><td>" + change.doc.data().VictimName + "</td></tr>"
//                    }
//                }
//
//            });
//
//        });
    }
    
    
    
}


function onChangeMonth(){
    
    createIncidentTable();
    createPie();
    createBar();
}

function onChangeCategory(){
    
    createIncidentTable();
}

function printPage() {
  window.print();
}






