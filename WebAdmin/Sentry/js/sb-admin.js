(function($) {
  "use strict"; // Start of use strict

  // Toggle the side navigation
  $("#sidebarToggle").on('click',function(e) {
    e.preventDefault();
    $("body").toggleClass("sidebar-toggled");
    $(".sidebar").toggleClass("toggled");
  });

  // Prevent the content wrapper from scrolling when the fixed side navigation hovered over
  $('body.fixed-nav .sidebar').on('mousewheel DOMMouseScroll wheel', function(e) {
    if ($(window).width() > 768) {
      var e0 = e.originalEvent,
        delta = e0.wheelDelta || -e0.detail;
      this.scrollTop += (delta < 0 ? 1 : -1) * 30;
      e.preventDefault();
    }
  });

  // Scroll to top button appear
  $(document).on('scroll',function() {
    var scrollDistance = $(this).scrollTop();
    if (scrollDistance > 100) {
      $('.scroll-to-top').fadeIn();
    } else {
      $('.scroll-to-top').fadeOut();
    }
  });

  // Smooth scrolling using jQuery easing
  $(document).on('click', 'a.scroll-to-top', function(event) {
    var $anchor = $(this);
    $('html, body').stop().animate({
      scrollTop: ($($anchor.attr('href')).offset().top)
    }, 1000, 'easeInOutExpo');
    event.preventDefault();
  });

})(jQuery); // End of use strict




var db = firebase.firestore();
var docRef = db.collection("Officers");
var userMobile = '123';
var selectedMobile = '123';
var userMobile = '123';
var validMobile = false;
var validName = false;
var numTest = '';
var numTestLength = 0;

const list_tbody = document.querySelector("#list_tbody");
db.collection("Officers").onSnapshot(function(querySnapshot) {
    
    querySnapshot.docChanges().forEach(function(change){
        
        if (change.type == "added"){
        
            list_tbody.innerHTML += "<tr><td class='numberclass'>" + change.doc.data().mobile + "</td><td>" + change.doc.data().name + "</td><td>" + change.doc.data().rank + "</td><td>" + change.doc.data().email + "</td>" + "<td class = 'text-center'><button type = 'button' class='inline' class='btnUpdate'>Update</button><button type = 'button' class='inline' class='btnDel'>Delete</button></td></tr>"
        }
        
    });

    
    $(".btnDel").click(function(){
        selectedMobile = $(this).closest("tr").find(".numberclass").text();

        docRef.get().then((snapshot) => {
        snapshot.forEach(function(docs){
            var docId = docs.id.toString();
            userMobile = docs.data().mobile;
            
            if(userMobile === selectedMobile ){
                docRef.doc(docId).delete();
                alert("Record deleted");
            }
        });

        });
    });  
    
    $(".btnUpdate").click(function(){
        selectedMobile = $(this).closest("tr").find(".numberclass").text();
        setNumber();
        document.location = 'update.html';
        
    });  
});


function storeData(){
    
    var inputMobile = document.getElementById("inputMobile").value;  
    var inputFirstName = document.getElementById("inputFirstname").value; 
    var inputLastName = document.getElementById("inputLastname").value; 
    var inputEmail = document.getElementById("inputEmail").value; 
    
    var rank = document.getElementById("inputRank");
    var inputRank = rank.options[rank.selectedIndex].text;
    
    checkMobile();
    checkName();
    
    if (validName === true && validMobile === true){
        docRef.doc().set({
        mobile: '+63' + inputMobile,
        rank: inputRank,
        name: inputFirstName + ' ' + inputLastName,
        email: inputEmail,
        Latitude: 0.0,
        Longitude: 0.0,
        hidden: false
        })
        
        $("#addModal").modal("show");
    }
    else {
        alert("error");
    }
}



function checkName(){    
    var nameTest = /^[A-Za-z ]+$/;

    if(nameTest.test(document.getElementById("inputFirstName").value) && nameTest.test(document.getElementById("inputLastName").value)){
        validName = true;
    }
       
    else{
        alert('Enter a valid name'); 
        validName = false;
    }   
}

function checkMobile(){
    numTest = document.getElementById("inputMobile").value;
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
