window.Karte = (function () {

	var preferences = {
		url : "http://karte.dominique-bosselmann.de/"
	};
	
	var controller = {
		uiElements : {
			toolbar : null,
			searchButton: null,
			exportButton: null,
			importButton: null,
			saveButton: null,
			searchField: null,
			mapChooser: null,
			activeMapChooser: null
		},
		init : function () {
			// UI-Elemente mit Referenzen versehen
			this.uiElements.toolbar = document.getElementById("toolbar");
			this.uiElements.searchButton = document.querySelector("button[title='Suchen']");
			this.uiElements.exportButton = document.querySelector("button[title='Exportieren']");
			this.uiElements.importButton = document.querySelector("button[title='Importieren']");
			this.uiElements.saveButton = document.querySelector("button[title='Sichern']");
			this.uiElements.searchField = document.getElementById("searchField");
			this.uiElements.mapChooser = Array.prototype.slice.call(document.getElementById("choosemap").children);
			
			// EventListener hinzufügen
			this.uiElements.searchButton.addEventListener("click", this.handler.enableSearch, false);
			this.uiElements.exportButton.addEventListener("click", function (e) { console.log(e); }, false);
			this.uiElements.importButton.addEventListener("click", function (e) { console.log(e); }, false);
			this.uiElements.saveButton.addEventListener("click", function (e) { console.log(e); }, false);
			
			this.uiElements.searchField.addEventListener("keyup", controller.handler.handleSearchInput, false);
			this.uiElements.searchField.addEventListener("keydown", controller.handler.handleSearchInput, false);
			
			this.uiElements.mapChooser.forEach(function (element) {				
				element.addEventListener("click", controller.handler.switchMapView, false);
				
				// Aktive View setzen
				if (element.className === "active") {
					controller.uiElements.activeMapChooser = element;
				}
			});
			
			// Karte laden
			this.loadMap({
				onSuccess : function (event) {
					console.log(event);
				},
				onError : function (event) {
					console.error(event);
				}
			});
		},
		handler : {
			enableSearch : function (event) {
				// Suchfeld einblenden und fokussieren
				controller.uiElements.toolbar.className = "searchEnabled";
				controller.uiElements.searchField.focus();
				
				controller.uiElements.searchButton.removeEventListener("click", controller.handler.enableSearch, false);
			},
			handleSearchInput : function (event) {
				if (event.type === "keydown" && event.keyCode === 27) {
					if (controller.uiElements.searchField.value === "") {
						controller.handler.disableSearch();
					}
				} else if (event.type === "keyup" && event.keyCode === 13) {
					controller.handler.performSearch();
				}
			},
			performSearch : function (event) {
				alert("Du suchst also nach \"" + controller.uiElements.searchField.value + "\". Wenn jetzt die Suchfunktion funktionieren würde...");
			},
			disableSearch : function () {
				controller.uiElements.toolbar.className = "";
				controller.uiElements.searchButton.addEventListener("click", controller.handler.enableSearch, false);
			},
			switchMapView : function (event) {
				// Überprüfung ob die View geändert wurde
				if (event.currentTarget === controller.uiElements.activeMapChooser) {
					return;
				}
				
				// Aktive View setzen
				controller.uiElements.activeMapChooser = event.currentTarget;
				
				// MapView ändern in den Einstellungen
				alert("Wenn jetzt ne Karte da wäre, könnte man zwischen den Ansichten wechseln.");
				
				// UI-Rückmeldung
				controller.uiElements.mapChooser.forEach(function (element) {
					if (element === event.currentTarget) {
						element.className = "active";
					} else {
						element.className = "";
					}
				});
			}
		},
		loadMap : function (latitude, longitude, handler) {
			var parameters, request;
			
			parameters = "lat=" + latitude;
			parameters += "&long= " + longitude;
			
			request = new XMLHttpRequest();
			request.onreadystatechange = function () {
				if (request.readyState === 4) {
					handler.onSuccess({
						statusCode : request.statusCode;
						data: request.responseXML;
					});
				}
			}
			request.open("get", preferemces.url + "get/map?" + parameters, true);
			request.send(null);
			
			console.log("Die Karte wird geladen");
		}
	};
	
	var renderer = {
			
	};
	
	var cache = {
	
	};
	
	return {
		init: function () {
			controller.init();
		}
	}
	
})();

window.addEventListener("DOMContentLoaded", function() {
	Karte.init();
}, false);