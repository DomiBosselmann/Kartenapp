window.Karte = (function () {

	var constants = {
		url : "http://karte.localhost/backend/transformation/steffen/php/svg_market.php"
		//url : "http://karte.localhost/backend/transformation/steffen/php/transform.php"
	};
	
	var map = {
		coordinates : {
			topleft: [],
			bottomright : []
		},
		scaling : {
			value : 4,
			unit : "km",
			scalerX : null,
			scalablesX : []
		},
		layers : {
			roads : {
				name : "Straßen",
				visible : false,
				paramName : undefined,
				sub : {
					motorways : {
						name : "Autobahnen",
						visible : true,
						paramName : "m"
					},
					federal : {
						name : "Bundesstraßen",
						visible : true,
						paramName : undefined
					},
					landesstrasse : {
						name : "Landesstraße",
						visible : false,
						paramName : undefined
					},
					kreisstrasse : {
						name : "Kreisstraße",
						visible : false,
						paramName : undefined
					}
				}
			},
			places : {
				name : "Städte",
				visible : false,
				sub : {
					cities : {
						name : "Städte",
						visible : true,
						paramName : "c"
					},
					towns : {
						name : "(Dörfer)",
						visible : false,
						paramName : "c1"
					},
					villages : {
						name : "(Kuhdörfer)",
						visible : false,
						paramName : "c2"
					},
					hamlets : {
						name : "(Kaffs)",
						visible : false,
						paramName : "c3"
					},
					suburbs : {
						name : "(Bauernhof)",
						visible : false,
						paramName : "c4"
					}
				}
			},
			boundaries : {
				name : "Grenzen",
				visible : true,
				paramName : undefined,
				sub : {
					federal : {
						name : "Länder",
						visible : true,
						paramName : "b"
					},
					counties : {
						name : "Landkreise",
						visible : false,
						paramName : "b1"
					}
				}
			},
			rivers : {
				name : "Seen und Flüsse",
				visibile : false,
				paramName : undefined,
				sub : {
					rivers : {
						name : "Flüsse",
						visible : true,
						paramName : "r"
					}
				}
			}
		},
		places : [],
		routes : []
	}
	
	var controller = {
		uiElements : {
			toolbar : null,
			searchButton: null,
			exportButton: null,
			importButton: null,
			saveButton: null,
			searchField: null,
			mapChooser: null,
			activeMapChooser: null,
			map: null
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
			this.uiElements.map = document.getElementById("mapcontainer");
			this.uiElements.mapScale = document.getElementById("mapscale");
			this.uiElements.mapScaler = document.getElementById("scaler");
			this.uiElements.mapScaleText = document.getElementById("scalevalue");
			this.uiElements.scalables = this.uiElements.mapScale.getElementsByClassName("scalable");
			this.uiElements.visibilities = document.getElementById("visibilities");
			
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
			
			this.uiElements.mapScaler.addEventListener("mousedown", this.handler.enableScaling, false);
			
			// Attribute für Geschwindigkeit zwischenspeichern
			var length = controller.uiElements.scalables.length;
			for (var i = 0; i < length; i++) {
				map.scaling.scalablesX.push(parseInt(controller.uiElements.scalables[i].getAttribute("x")));
			}
						
			// Karte laden
			this.loadMap(undefined, undefined, {
				onSuccess : function (event) {
					controller.uiElements.map.appendChild(event.data.getElementsByTagName("svg")[0]);
				},
				onError : function (event) {
					console.error(event);
				}
			});
			
			// Ansichten anzeigen
			sideView.renderVisibilities();
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
			},
			enableScaling : function (event) {
				map.scaling.scalerX = event.pageX;
				map.scaling.scalerY = event.pageY;
				
				controller.uiElements.mapScale.setAttribute("class","");
				
				document.addEventListener("mousemove", controller.handler.handleScaling, false);
				document.addEventListener("mouseup", controller.handler.finishScaling, false);
			},
			handleScaling : function (event) {
				// Karte skalieren	
				
				// Maßstab-UI anpassen
				var transform = "translate(%d)",
					transformValue;
				
				var diff = event.pageX - map.scaling.scalerX;
				var scaleValue = Math.round((map.scaling.value/Math.abs(100 - diff)) * 10000) / 100;
				
				console.log(scaleValue);
				controller.uiElements.mapScaleText.textContent = scaleValue + map.scaling.unit;
				
				var length = controller.uiElements.scalables.length;
				
				for (var i = 0; i < length; i++) {
					transformValue = (event.pageX - map.scaling.scalerX) * (i + 1)/4;
					controller.uiElements.scalables[i].setAttribute("transform",transform.replace(/%d/g, transformValue));
				}
			},
			finishScaling : function (event) {
				// Bei Bedarf neue Daten laden
				
				// Maßstab-UI anpassen
				var diff = event.pageX - map.scaling.scalerX;
				map.scaling.value = (map.scaling.value/Math.abs(100 - diff)) * 100;
				
				controller.uiElements.mapScale.setAttribute("class","finishScaling");
				
				//debugger;
				
				window.setTimeout(function () {
					var length = controller.uiElements.scalables.length;
				
					for (var i = 0; i < length; i++) {
						controller.uiElements.scalables[i].removeAttribute("transform");
					}
				}, 20);

				
				document.removeEventListener("mousemove", controller.handler.handleScaling, false);
			},
			setVisibility : function (event, object) {
				object.visible = object.visible ? false : true;
				event.currentTarget.className = object.visible ? "active" : "inactive";
				
				renderer.filter();
			}
		},
		loadMap : function (latitude, longitude, handler) {
			
			var parameters, params = [], requestURL = constants.url, request,
				layers = [],
				key;
							
			// Layer bestimmen
			
			for (type in map.layers) {
				if (map.layers.hasOwnProperty(type) && map.layers[type].visible) {
					for (subtype in map.layers[type].sub) {
						if (map.layers[type].sub.hasOwnProperty(subtype) && map.layers[type].sub[subtype].visible && map.layers[type].sub[subtype].paramName !== undefined) {
							layers.push(map.layers[type].sub[subtype].paramName);
						}
					}
				}
			}
			
			console.log(layers);
			
			// Parameter für die Übergabe zusammenschustern
			
			parameters = {
				lat : latitude,
				long : longitude,
			};
			
			layers.forEach(function (value, index) {
				parameters[index + 1] = value;
			});
			
			for (key in parameters) {
				if (parameters.hasOwnProperty(key) && parameters[key] !== undefined) {
					params.push(encodeURIComponent(key) + "=" + encodeURIComponent(parameters[key]));
				}
			};
			
			if (params.length !== 0) {
				requestURL += "?" + params.join("&");
			}
			
			// Request absetzen
			
			request = new XMLHttpRequest();
			request.open("get", requestURL, true);
			request.send(null);
			request.onreadystatechange = function () {
				if (request.readyState === 4) {
					handler.onSuccess({
						statusCode : request.statusCode,
						data: request.responseXML,
						textData: request.responseText
					});
				}
			}	
		}
	};
	
	var renderer = {
		
	};
	
	var sideView = {
		renderVisibilities : function () {
			var type, subtype, value,
				title, list, item;
			
			for (type in map.layers) {
				if (map.layers.hasOwnProperty(type)) {
					title = document.createElement("h1");
					title.textContent = map.layers[type].name;
					controller.uiElements.visibilities.appendChild(title);
					
					list = document.createElement("ul");
					controller.uiElements.visibilities.appendChild(list);
										
					for (subtype in map.layers[type].sub) {
						if (map.layers[type].sub.hasOwnProperty(subtype)) {
							value = map.layers[type].sub[subtype];
							item = document.createElement("li");
							item.className = value.visible ? "active" : "inactive";
							
							(function (subtype) {
								item.addEventListener("click", function (event) { controller.handler.setVisibility(event, subtype); }, false);
							})(value);
							
							item.textContent = value.name;
							list.appendChild(item);
						}
					}
				}
			}
		}
	}
	
	return {
		init: function () {
			controller.init();
		}
	}
	
})();

window.addEventListener("DOMContentLoaded", function() {
	Karte.init();
}, false);