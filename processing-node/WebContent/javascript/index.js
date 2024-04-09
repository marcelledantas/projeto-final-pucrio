/**
 * @author meslin
 */

onload = function() {
	getAreas();
	document.getElementById("idSubmitAlert").addEventListener("click", submitAlert);
}

/**
 * Submit an alert to be stored in InterSCity<br>
 * areas=1&areas=3&areas=4&startTimestamp=2019-03-08T17%3A23&text=Alert+description&endTimestamp=2019-03-08T18%3A00&time=5<br>
 */
function submitAlert() {
	// erase the status waiting for an update
	setStatus("Please, wait.");
	
	// create the query string
	var qs = "startTimestamp=" + encodeURIComponent(document.getElementById("idStartTimestamp").value);
	qs += "&endTimestamp="     + encodeURIComponent(document.getElementById("idEndTimestamp").value);
	qs += "&text="             + encodeURIComponent(document.getElementById("idText").value);
	for(var i=0; i<document.getElementById('idAreas').options.length; i++) {
		if(document.getElementById('idAreas').options[i].selected) {
			qs += "&areas=" + encodeURIComponent(
					document.getElementById('idAreas').options[i].value + "#" + 
			        document.getElementById("idAreas").options[i].getAttribute("data-lat") + "#" +
			        document.getElementById("idAreas").options[i].getAttribute("data-lon"));
		}
	}
	
	// create the http request object
	var xmlhttp = createRequest();
	if(xmlhttp == null) {
		alert("Browser not compatible with AJAX!");
		return;
	}
	
	console.log("Creating alert " + "PutAlert?" + qs);
	// send data to PutAlert with QS
	xmlhttp.open("get", "PutAlert?" + qs, true);
	xmlhttp.onreadystatechange = function() {
		if(xmlhttp.readyState == 4) {
			if(xmlhttp.status == 200) {
				setStatus(xmlhttp.responseText);
			} else {
				setStatus("Alert not created!");
				console.log("Alert not created because error " + xmlhttp.status);
			}
		}
	}
	xmlhttp.send(null);
}

/**
 * Gets areas name and number in JSON format<br>
 * Must be called once, because this function does not clear the select field<br>
 * <pre>
 * {<br>
 * 	regions: [<br>
 * 		{<br>
 * 			number: <i>number</i><br>
 * 			name: <i>name</i><br>
 * 		}, ...
 * 	]<br>
 * }
 * </pre>
 */
function getAreas() {
	// erase the status waiting for an update
	setStatus("Loading areas...");
	
	var xmlhttp = createRequest();
	if(xmlhttp == null) {
		alert("Browser not compatible with AJAX!");
		return;HH
	}
	xmlhttp.open("get", "GetRegion", true);
	xmlhttp.onreadystatechange = function() {
		if(xmlhttp.readyState == 4) {
			if(xmlhttp.status == 200) {
				var fieldAreas = document.getElementById("idAreas");
				var regions = JSON.parse(xmlhttp.responseText);
				for(var i=0; i<regions.regions.length; i++) {
					var option = document.createElement("option");
					option.value = regions.regions[i].number;
					option.setAttribute("data-lat", regions.regions[i].lat);
					option.setAttribute("data-lon", regions.regions[i].lon);
					option.text =  regions.regions[i].name.substring(14, regions.regions[i].name.length-6);
					fieldAreas.add(option);
				}
				setStatus("OK!");
			} else {
				setStatus("NOK??");
			}
		}
	}
	xmlhttp.send(null);
}

/**
 * Set the status text
 * @param text
 */
function setStatus(text) {
	var divStatus = document.getElementById("idStatus");
	divStatus.replaceChild(document.createTextNode(text), divStatus.firstChild);	
}