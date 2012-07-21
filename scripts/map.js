window.Karte = (function () {

	var constants = {
		url : "http://karte.localhost/backend/php/svg_market.php"
	};
	
	var map = {
		isLoaded : false,
		dimensions : {
			width : undefined,
			height : undefined,
			maxWidth : undefined,
			maxHeight : undefined
		},
		coordinates : {
			topLeft: [],
			bottomRight : [],
			center : []
		},
		scaling : {
			value : undefined, // (Pro 100 Pixel)
			zoomLevelValue : undefined,
			unit : "km",
			scalerX : null,
			scalablesX : [],
			scaleFactor : 1
		},
		panning : {
			x : 0,
			y : 0,
			startX : 0,
			startY : 0,
		},
		layers : {
			places : {
				name : "Städte",
				visible : true,
				sub : {
					cities : {
						name : "Städte",
						visible : true,
						paramName : "c",
						layerName : "cities",
						zIndex : 15
					},
					towns : {
						name : "(Dörfer)",
						visible : false,
						paramName : "c1",
						layerName : "towns",
						zIndex : 14
					},
					villages : {
						name : "(Kuhdörfer)",
						visible : false,
						paramName : "c2",
						layerName : "villages",
						zIndex : 13
					},
					hamlets : {
						name : "(Kaffs)",
						visible : false,
						paramName : "c3",
						layerName : "hamlets",
						zIndex : 12
					},
					suburbs : {
						name : "(Bauernhof)",
						visible : false,
						paramName : "c4",
						layerName : "suburbs",
						zIndex : 11
					}
				}
			},
			boundaries : {
				name : "Grenzen",
				visible : true,
				paramName : undefined,
				sub : {
					federal : {
						name : "Bundesländer",
						visible : true,
						paramName : "b",
						layerName : "federal",
						zIndex : 1
					},
					counties : {
						name : "Landkreise",
						visible : false,
						paramName : "b1",
						layerName : "counties",
						zIndex : 2
					}
				}
			},
			roads : {
				name : "Straßen",
				visible : true,
				paramName : undefined,
				sub : {
					motorways : {
						name : "Autobahnen",
						visible : true,
						paramName : "s",
						layerName : "motorways",
						zIndex : 10
					},
					federal : {
						name : "(B&K)",
						visible : false,
						paramName : "s1",
						layerName : "primaries",
						zIndex : 9
					},
					
					landesstrasse : {
						name : "Landesstraße",
						visible : false,
						paramName : "s2",
						layerName : "secondaries",
						zIndex : 8
					},
					kreisstrasse : {
						name : "Kreisstraße",
						visible : false,
						paramName : "s3",
						layerName : "tertiaries",
						zIndex : 7
					}
				}
			},
			waters : {
				name : "Gewässer",
				visible : true,
				paramName : undefined,
				sub : {
					rivers : {
						name : "Flüsse",
						visible : true,
						paramName : "w",
						layerName : "rivers",
						zIndex : 6
					},
					canals : {
						name : "Kanäle",
						visible : false,
						paramName : "w1",
						layerName : "canals",
						zIndex : 5
					},
					namedLakes : {
						name : "Seen",
						visible : false,
						paramName : "w2",
						layerName : "namedLakes",
						zIndex : 4
					},
					allLakes : {
						name : "(Seen)",
						visible : false,
						paramName : "w3",
						layerName : "allLakes",
						zIndex : 3
					}
				}
			}
		},
		places : [
			{
				name : "Feierabendweg 17",
				visible : true,
				note : "Mein Zuhause",
				coordinates : [],
				pinReference : undefined,
				listReference : undefined
			},
			{
				name : "Erzbergerstraße 121",
				visible : true,
				note : "Meine Uni",
				coordinates : [],
				pinReference : undefined,
				listReference : undefined
			},
			{
				name : "Medienallee 1",
				visible : true,
				note : "Mein Lieblingsfernsehsender",
				coordinates : [],
				pinReference : undefined,
				listReference : undefined
			},
			{
				name : "Dietmar-Hopp-Alle 2",
				visible : true,
				note : "Mein Arbeitsplatz",
				coordinates : [],
				pinReference : undefined,
				listReference : undefined
			},
		],
		routes : [
			{
				name : "Weg zur Hochschule",
				distance : "3km",
				note : "Blafaselblubber",
				points : [],
				visible : true,
				listReference : undefined
			}
		]
	}
	
	var controller = {
		uiElements : {
			toolbar : null,
			addButton : null,
			searchButton: null,
			exportButton: null,
			importButton: null,
			saveButton: null,
			searchField: null,
			map: null,
			mapRoot : null,
			mapScale : null,
			mapScaler : null,
			mapScaleText : null,
			scalables : null,
			visibilities : null,
			places : null,
			routes : null
		},
		init : function () {
			// UI-Elemente mit Referenzen versehen
			this.uiElements.toolbar = document.getElementById("toolbar");
			this.uiElements.addButton = document.querySelector("button[title='Route oder Punkt hinzufügen']");
			this.uiElements.searchButton = document.querySelector("button[title='Suchen']");
			this.uiElements.exportButton = document.querySelector("button[title='Exportieren']");
			this.uiElements.importButton = document.querySelector("button[title='Importieren']");
			this.uiElements.saveButton = document.querySelector("button[title='Sichern']");
			this.uiElements.searchField = document.getElementById("searchField");
			this.uiElements.map = document.getElementById("mapcontainer");
			this.uiElements.mapRoot = this.uiElements.map.getElementsByTagName("svg")[0];
			this.uiElements.mapScale = document.getElementById("mapscale");
			this.uiElements.mapScaler = document.getElementById("scaler");
			this.uiElements.mapScaleText = document.getElementById("scalevalue");
			this.uiElements.scalables = this.uiElements.mapScale.getElementsByClassName("scalable");
			this.uiElements.visibilities = document.getElementById("visibilities");
			this.uiElements.places = document.getElementById("places");
			this.uiElements.routes = document.getElementById("routes");
			
			// EventListener hinzufügen
			this.uiElements.addButton.addEventListener("click", this.handler.flags.enableAddSelection, false);
			this.uiElements.searchButton.addEventListener("click", this.handler.handleSearch, false);
			this.uiElements.exportButton.addEventListener("click", function (e) { console.log(e); }, false);
			this.uiElements.importButton.addEventListener("click", function (e) { console.log(e); }, false);
			this.uiElements.saveButton.addEventListener("click", function (e) { console.log(e); }, false);
			
			this.uiElements.searchField.addEventListener("keyup", controller.handler.handleSearchInput, false);
			this.uiElements.searchField.addEventListener("keydown", controller.handler.handleSearchInput, false);
			
			this.uiElements.mapScaler.addEventListener("mousedown", this.handler.enableScaling, false);
			
			this.uiElements.map.addEventListener("mousedown", this.handler.enablePanning, false);
			
			// Attribute für Geschwindigkeit zwischenspeichern
			var length = controller.uiElements.scalables.length;
			for (var i = 0; i < length; i++) {
				map.scaling.scalablesX.push(parseInt(controller.uiElements.scalables[i].getAttribute("x")));
			}
			
			// Dimensionen herausfinden und setzen
			map.dimensions.width = window.innerWidth - 320;
			map.dimensions.height = window.innerHeight;
			map.dimensions.maxWidth = screen.availWidth - 320;
			map.dimensions.maxHeight = screen.availHeight;
			
			console.log(map);
			
			// Automatische UI-Änderungen
			map.scaling.observe("value",function (event) {
				controller.uiElements.mapScaleText.textContent = Math.round(event.data * 100) / 100 + map.scaling.unit;
			});
			
			// Benötigte Layer bestimmen
			var layers = [];
					
			for (type in map.layers) {
				if (map.layers.hasOwnProperty(type) && map.layers[type].visible) {
					for (subtype in map.layers[type].sub) {
						if (map.layers[type].sub.hasOwnProperty(subtype) && map.layers[type].sub[subtype].visible && map.layers[type].sub[subtype].paramName !== undefined) {
							layers[map.layers[type].sub[subtype].zIndex] = map.layers[type].sub[subtype].paramName;
						}
					}
				}
			}
						
			// Karte laden
			this.loadMap(undefined, undefined, layers, {
				onSuccess : controller.handler.loadMap,
				onError : function (event) {
					alert("tja, iwie blöd gelaufen");
				}
			});
			
			// Ansichten anzeigen
			sideView.render();
			
			// Tastatur-Navigation einrichten
			document.addEventListener("keyup", controller.handler.keyboardNavigation, false);
		},
		handler : {
			handleSearch : function (event) {
				if (event.altKey) {
					controller.uiElements.searchField.value = "";
					sideView.renderFlags(true);
					sideView.renderFlags(false);
				} else {
					controller.handler.enableSearch(event);
				}
			},
			enableSearch : function (event) {
				// Suchfeld einblenden und fokussieren
				controller.uiElements.toolbar.className = "searchEnabled";
				controller.uiElements.searchField.addEventListener("blur", controller.handler.disableSearch, false);
				controller.uiElements.searchField.focus();
				
				controller.uiElements.searchButton.removeEventListener("click", controller.handler.enableSearch, false);
			},
			handleSearchInput : function (event) {
				if (event.type === "keydown" && event.keyCode === 27) {
					if (controller.uiElements.searchField.value === "") {
						controller.handler.disableSearch();
					}
				} else if (event.type === "keyup") {
					controller.handler.performSearch();
				}
			},
			performSearch : function (event) {
				var filteredData = [];
				
				if (controller.uiElements.searchField.value === "") {
					// Wenn Suchfeld leer ist, kann die ganze View gerendert werden
					sideView.renderFlags(false);
					sideView.renderFlags(true);
				} else {
					// Suchausdruck durchsucht Namen und Notitz
					// Zunächst werden die Orte durchsucht
					filteredData = Sort.filter(map.places, [{ attribute : "note", type : "contains", value : controller.uiElements.searchField.value }]);
					filteredData = filteredData.concat(Sort.filter(map.places, [{ attribute : "name", type : "contains", value : controller.uiElements.searchField.value }]));
					
					filteredData = filteredData.unique();
										
					sideView.renderFlags(false, filteredData);
					
					// Jetzt werden die Strecken durchsucht
					filteredData = Sort.filter(map.routes, [{ attribute : "note", type : "contains", value : controller.uiElements.searchField.value }]);
					filteredData = filteredData.concat(Sort.filter(map.routes, [{ attribute : "name", type : "contains", value : controller.uiElements.searchField.value }]));
					
					filteredData = filteredData.unique();
					
					sideView.renderFlags(true, filteredData);
				}			
			},
			disableSearch : function () {
				controller.uiElements.toolbar.className = "";
				controller.uiElements.searchButton.addEventListener("click", controller.handler.handleSearch, false);
			},
			enableScaling : function (event) {
				map.scaling.scalerX = event.pageX;
				map.scaling.scalerY = event.pageY;
				
				controller.uiElements.mapScale.setAttribute("class","");
				
				document.addEventListener("mousemove", controller.handler.handleScaling, false);
				document.addEventListener("mouseup", controller.handler.finishScaling, false);
			},
			handleScaling : function (event) {
				// Aktuellen Maßstab berechnen
				var diff = event.pageX - map.scaling.scalerX;
				var scaleValue = Math.round((map.scaling.value/Math.abs(100 - diff)) * 10000) / 100;
			
				// Karte skalieren
				
				renderer.zoom(map.scaling.zoomLevelValue, scaleValue);
								
				// Maßstab-UI anpassen
				var transform = "translate(%d)",
					transformValue;
				
				// Zoom-Level wegschreiben
				map.scaling.scaleFactor = map.scaling.zoomLevelValue / scaleValue;
				console.log(map.scaling.scaleFactor);
				
				controller.uiElements.mapScaleText.textContent = scaleValue + map.scaling.unit;
				
				var length = controller.uiElements.scalables.length;
				
				for (var i = 0; i < length; i++) {
					transformValue = (event.pageX - map.scaling.scalerX) * (i + 1)/4;
					controller.uiElements.scalables[i].setAttribute("transform",transform.replace(/%d/g, transformValue));
				}
			},
			finishScaling : function (event) {
				var diff = event.pageX - map.scaling.scalerX
				map.scaling.value = (map.scaling.value/Math.abs(100 - diff)) * 100;
				
				// Bei Bedarf neue Daten laden — TODO: Bedarf ermitteln
								
				// Maßstab-UI anpassen
				
				controller.uiElements.mapScale.setAttribute("class","finishScaling");
								
				window.setTimeout(function () {
					var length = controller.uiElements.scalables.length;
				
					for (var i = 0; i < length; i++) {
						controller.uiElements.scalables[i].removeAttribute("transform");
					}
				}, 20);

				
				document.removeEventListener("mousemove", controller.handler.handleScaling, false);
			},
			enablePanning : function (event) {
				map.panning.startX = event.pageX - map.panning.x;
				map.panning.startY = event.pageY - map.panning.y;
			
				document.addEventListener("mousemove", controller.handler.handlePanning, false);
				document.addEventListener("mouseup", controller.handler.finishPanning, false);
			},
			handlePanning : function (event) {
				map.panning.x = event.pageX - map.panning.startX;
				map.panning.y = event.pageY - map.panning.startY;
								
				renderer.pan(map.panning.x, map.panning.y);				
			},
			finishPanning : function (event) {
				map.panning.x = event.pageX - map.panning.startX;
				map.panning.y = event.pageY - map.panning.startY;
				
				// EventListener wieder entfernen
				document.removeEventListener("mousemove", controller.handler.handlePanning, false);
				document.removeEventListener("mouseup", controller.handler.finsihPanning, false);
			},
			setVisibility : function (event, object) {
				object.visible = object.visible ? false : true;
				event.currentTarget.className = object.visible ? "active" : "inactive";
				
				renderer.filter(object, !object.visible);
			},
			loadMap : function (event) {
				//console.log(event.textData);
				var data = event.data,
					coordinates = data.getElementsByTagName("coords")[0];
				
				// Koordinaten auslesen
				map.coordinates.topLeft = [parseFloat(coordinates.getAttribute("lat1")), parseFloat(coordinates.getAttribute("lon1"))];
				map.coordinates.bottomRight = [parseFloat(coordinates.getAttribute("lat2")), parseFloat(coordinates.getAttribute("lon2"))];
				//map.coordinates.center = [(map.coordinates.topLeft[0] - map.coordinates.bottomRight[0])/2 + map.coordinates.topLeft[0], (map.coordinates.topLeft[1] - map.coordinates.bottomRight[1])/2 + map.coordinates.topLeft[1]];
				
				// Maßstab neu berechnen
				map.scaling.value = units.geoCoordinatesToDistance(map.coordinates.topLeft, map.coordinates.bottomRight) / map.dimensions.width * 100;
				map.scaling.zoomLevelValue = map.scaling.value;
									
				// Renderer anstoßen
				renderer.start(data);
			},
			flags : {
				viewList : undefined,
				pin : undefined,
				x : undefined,
				y : undefined,
				altKey : undefined,
				pinObject : undefined,
				panningDiffX : undefined,
				panningDiffY : undefined,
				newPlace : function (event) {
					if (event.currentTarget !== event.target) { // Event war im Zeichenbereich von Bawü und nicht auf einem Pin
						/*  && !(event.target instanceof SVGElementInstance && event.target.correspondingUseElement.parentNode === renderer.flags) */
						
						controller.handler.flags.altKey = event.altKey;
						controller.handler.flags.pin = document.createElementNS( "http://www.w3.org/2000/svg", "use");
						
						// 1 Für Chrome + Safarie 2 für FF
						controller.handler.flags.x = event.offsetX ? event.offsetX : event.layerX;
						controller.handler.flags.y = event.offsetY ? event.offsetY : event.layerY;
						
						renderer.drawPin(controller.handler.flags.pin, controller.handler.flags.x, controller.handler.flags.y);
						
						controller.handler.flags.pin.setAttribute("class", "hover");
						
						/*pin.addEventListener("click", function(e) {
							//Verhindere, dass das Event im SVG Element ausgelößt wird
							e.stopPropagation();
						});*/
						
						controller.handler.flags.pin.addEventListener("mousedown", controller.handler.flags.enablePanning, false);
					
						controller.handler.flags.viewList = document.createElement("li");
						controller.handler.flags.viewList.contentEditable = true;
						controller.handler.flags.viewList.addEventListener("keypress", controller.handler.flags.finishAddNewPlace, false);
						controller.handler.flags.viewList.addEventListener("keyup", function (event) {
							if (event.keyCode === 27) {
								// Es war ein Esc
								controller.handler.flags.abortAddNewPlace(event);
							} else {
								controller.handler.flags.finishAddNewPlace(event);
							}
						}, false);
						controller.handler.flags.viewList.addEventListener("mouseover", controller.handler.flags.highlightPin, false);
						controller.handler.flags.viewList.addEventListener("mouseout", controller.handler.flags.deHighlightPin, false);
						controller.handler.flags.viewList.addEventListener("click", controller.handler.flags.setVisibility, false);
						controller.handler.flags.viewList.addEventListener("blur", controller.handler.flags.abortAddNewPlace, false);
						
						controller.uiElements.places.appendChild(controller.handler.flags.viewList);
						controller.handler.flags.viewList.focus();
					}
				},
				highlightPin : function (event) {
					map.places[event.currentTarget.getAttribute("data-interimPinID") - 1].pinReference.setAttribute("class", "hover");
				},
				highlightListView : function (event) {
					map.places[event.currentTarget.getAttribute("data-interimPinID") - 1].listReference.classList.add("hover");
				},
				deHighlightPin : function (event) { // Super Funktionsname
					map.places[event.currentTarget.getAttribute("data-interimPinID") - 1].pinReference.removeAttribute("class");
				},
				deHighlightListView : function (event) {
					map.places[event.currentTarget.getAttribute("data-interimPinID") - 1].listReference.classList.remove("hover");
				},
				finishAddNewPlace : function (event) {
					if (event.keyCode === 13 || event.keyCode === 39) {
						// Es war ein Enter. Oder ein Rechtspfeil
						
						// Blur-Handler entfernen (UI-Aktion für Abbruch)
						controller.handler.flags.viewList.removeEventListener("blur", controller.handler.flags.abortAddNewPlace, false);
						
						controller.handler.flags.viewList.blur();
						controller.handler.flags.pin.removeAttribute("class");
						controller.handler.flags.viewList.contentEditable = false;
						
						// Neuen Ort wegschreiben
						controller.handler.flags.pinObject = {
							name : controller.handler.flags.viewList.textContent,
							visible : true,
							note : "",
							coordinates : units.pixelCoordinateToGeoCoordinate(controller.handler.flags.x, controller.handler.flags.y),
							pinReference : controller.handler.flags.pin,
							listReference : controller.handler.flags.viewList
						};
						
						var pinID = map.places.push(controller.handler.flags.pinObject);
						
						// Pin mit ID versehen
						controller.handler.flags.pin.setAttribute("data-interimPinID", pinID);
						event.target.setAttribute("data-interimPinID", pinID);
						
						controller.handler.flags.pin.addEventListener("mouseover", controller.handler.flags.highlightListView, false);
						controller.handler.flags.pin.addEventListener("mouseout", controller.handler.flags.deHighlightListView, false);
						
						if (!controller.handler.flags.altKey && !event.shiftKey) {
							controller.handler.flags.finishAddNewFlag();
						}
					}
				},
				abortAddNewPlace : function (event) {
					controller.uiElements.places.removeChild(controller.handler.flags.viewList);
					renderer.removePin(controller.handler.flags.pin);
				},
				enablePanning : function (event) {
					// Aktuellen Translate ermitteln
					var transform, x = event.offsetX ? event.offsetX : event.layerX, y = event.offsetY ? event.offsetY : event.layerY;
					
					controller.handler.flags.pinObject = map.places[event.currentTarget.getAttribute("data-interimPinID") - 1];
					controller.handler.flags.viewList = controller.handler.flags.pinObject.listReference;
					controller.handler.flags.pin = controller.handler.flags.pinObject.pinReference;
					
					transform = controller.handler.flags.pin.transform.baseVal.getItem(0);
					if (transform.type == SVGTransform.SVG_TRANSFORM_TRANSLATE) {
						controller.handler.flags.panningDiffX = x - transform.matrix.e,
						controller.handler.flags.panningDiffY = y - transform.matrix.f;
      				}
      				      									
					document.addEventListener("mousemove", controller.handler.flags.performPanning, false);
					document.addEventListener("mouseup", controller.handler.flags.finishPanning, false);
					
					controller.handler.flags.pin.removeEventListener("mouseover", controller.handler.flags.highlightListView, false);
					controller.handler.flags.pin.removeEventListener("mouseout", controller.handler.flags.deHighlightListView, false);
					
					event.stopPropagation();
					event.preventDefault();
				},
				performPanning : function (event) {
					var x = (event.offsetX ? event.offsetX : event.layerX) - controller.handler.flags.panningDiffX;
					var y = (event.offsetY ? event.offsetY : event.layerY) - controller.handler.flags.panningDiffY;
					controller.handler.flags.pin.setAttribute("transform", "translate(" + x + " " + y + ") scale(0.1)");
					
					event.stopPropagation();
					event.preventDefault();		
				},
				finishPanning : function (event) {
					var x = (event.offsetX ? event.offsetX : event.layerX) - controller.handler.flags.panningDiffX;
					var y = (event.offsetY ? event.offsetY : event.layerY) - controller.handler.flags.panningDiffY;
					
					controller.handler.flags.pinObject.coordinates = units.pixelCoordinateToGeoCoordinate(x, y);
					
					document.removeEventListener("mousemove", controller.handler.flags.performPanning, false);
					document.removeEventListener("mouseup", controller.handler.flags.finishPanning, false);
					
					event.stopPropagation();
					event.preventDefault();
					
					controller.handler.flags.viewList.classList.remove("hover");
					
					controller.handler.flags.pin.addEventListener("mouseover", controller.handler.flags.highlightListView, false);
					controller.handler.flags.pin.addEventListener("mouseout", controller.handler.flags.deHighlightListView, false);
				},
				setVisibility : function (event) {
					controller.handler.flags.pinObject.visible = controller.handler.flags.pinObject.visible ? false : true;
					event.currentTarget.className = controller.handler.flags.pinObject.visible ? "active" : "inactive";
					
					controller.handler.flags.pinObject.pinReference.style.display = controller.handler.flags.pinObject.visible ? "" : "none"; // Sollte eventuell in den Renderer
				},
				enableAddSelection : function (event) {
					var container = document.createElement("ul");
					var newPlace = document.createElement("li");
					var newRoute = document.createElement("li");
					
					container.id = "addSelectionMask";
					
					newPlace.textContent = "Neuer Ort";
					newPlace.addEventListener("click", controller.handler.flags.newFlag, false);
					
					newRoute.textContent = "Neue Route";
					newRoute.addEventListener("click", controller.handler.flags.newRoute, false);
					
					container.appendChild(newPlace);
					container.appendChild(newRoute);
					
					document.body.appendChild(container); // Einbindung ändern
					
					event.stopPropagation();
					
					document.addEventListener("click", controller.handler.flags.handleAddSelection, false);
				},
				newFlag : function (event) {
					controller.uiElements.toolbar.className = "addFlagEnabled";
					controller.uiElements.mapRoot.addEventListener("click", controller.handler.flags.newPlace, false);
					
					document.addEventListener("keyup", controller.handler.flags.abortAddNewFlag, false);
				},
				newRoute : function (event) {
					console.log(event);
				},
				handleAddSelection : function (event) {
					// Sollte noch unbenannt werden
					if (event.currentTarget !== document.getElementById("addSelectionMask")) {
						controller.handler.flags.disableAddSelection();
					}
				},
				disableAddSelection : function () {
					document.body.removeChild(document.getElementById("addSelectionMask"));
					document.removeEventListener("click", controller.handler.flags.handleAddSelection, false);
				},
				abortAddNewFlag : function (event) {
					if (event.keyCode === 27) {
						controller.uiElements.toolbar.className = "";
						document.removeEventListener("keyup", controller.handler.flags.abortAddNewFlag, false);
						controller.uiElements.mapRoot.removeEventListener("click", controller.handler.flags.newPlace, false);
					}
				},
				finishAddNewFlag : function (event) {
					controller.uiElements.toolbar.className = "";
					document.removeEventListener("keyup", controller.handler.flags.abortAddNewFlag, false);
					controller.uiElements.mapRoot.removeEventListener("click", controller.handler.flags.newPlace, false);
				},
			},
			keyboardNavigation : function (event) {
				// Bei Pfeiltasten: Navigation durch die Karte
				if (map.isLoaded && event.keyCode >= 37 && event.keyCode <= 40) {
					var panValue = event.shiftKey ? 250 : (event.altKey ? 10 : 50);
					
					switch (event.keyCode) {
						case 37 : map.panning.x -= panValue; break; // Links
						case 38 : map.panning.y -= panValue; break; // Oben
						case 39 : map.panning.x += panValue; break; // Rechts
						case 40 : map.panning.y += panValue; break; // Unten
					}
					
					renderer.pan(map.panning.x, map.panning.y);
				}
			}
		},
		loadMap : function (latitude, longitude, layers, handler) {
			
			var parameters, params = [], requestURL = constants.url, request,
				key;
				
			map.isLoaded = false;
						
			// Parameter für die Übergabe zusammenschustern
			
			parameters = {
				lat : latitude,
				lon : longitude,
				width: map.dimensions.width,
				height : map.dimensions.height
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
					map.isLoaded = true;
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
		root : undefined,
		data : undefined,
		map : undefined,
		layers : undefined,
		flags : undefined,
		clipPath : undefined,
		hasClipPath : false,
		start : function (data) {
			// Verwaltungsmethode für den Renderer
			this.data = data;
			this.map = controller.uiElements.mapRoot;
			this.flags = controller.uiElements.mapRoot.getElementById("flags");
			this.clipPath = controller.uiElements.mapRoot.getElementById("borderClip");
			
			this.parse();
			this.render();
			this.optimize();
			
			this.layers = controller.uiElements.mapRoot.getElementsByTagName("g"); // Notwendigkeit?
		},
		parse : function () {
			this.root = this.data.getElementsByTagName("svg")[0];
			this.data = this.root.childNodes;
			
			if (this.root.getElementById("federal") !== null) {
				var path = this.root.getElementById("federal").childNodes[0].cloneNode();
				this.clipPath.appendChild(path);
				
				renderer.hasClipPath = true;
			}
		},
		render : function () {
			var data = Array.prototype.slice.call(this.data);
			
			data.forEach(function (node) {
				if (renderer.hasClipPath && node.id !== "federal") {
					node.setAttribute("clip-path", "url(#borderClip)");
				}
				
				controller.uiElements.mapRoot.insertBefore(node, renderer.flags);
			});
		},
		optimize : function () {
			// Dummy, wird gefüllt, wenn noch Zeit über ist
		},
		stop : function () {
			
		},
		filter : function (layer, filter) {
			var layerElement = controller.uiElements.mapRoot.getElementById(layer.layerName); // TODO: evtl auch document.getElementById()
			
			if (layerElement === null) {
				// Layer noch nicht verfügbar, nachladen
				controller.loadMap(undefined, undefined, [layer.paramName], { onSuccess : controller.handler.loadMap });
			} else {
				layerElement.style.display = filter ? "none" : "";
			}
		},
		zoom : function (newDistance, oldDistance) {
			var scaleFactor = (newDistance / oldDistance);
			
			controller.uiElements.mapRoot.style.cssText += "-webkit-transform: scale(" + scaleFactor + "); -moz-transform: scale(" + scaleFactor + "); -ms-transform: scale(" + scaleFactor + "); -o-transform: scale(" + scaleFactor + "); transform: scale(" + scaleFactor + "); ";
		},
		pan : function (x, y) {
			controller.uiElements.mapRoot.style.cssText += "left: " + x + "px; top: " + y + "px;";
		},
		drawPin : function (pin, x, y) {
			pin.setAttributeNS(null, "transform", "translate(" + x + " " + y + ") scale(0.1)"); 
			pin.setAttributeNS("http://www.w3.org/1999/xlink", "href", "#pin");
			
			renderer.flags.appendChild(pin);
		},
		drawRoute : function (pins) {
			
		},
		removePin : function (pin) {
			renderer.flags.removeChild(pin);
		}
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
		},
		renderFlags : function (routes, data) {
			var item, note, name,
				list = routes ? controller.uiElements.routes : controller.uiElements.places,
				content = data ? data : (routes ? map.routes : map.places);
				
			list.innerHTML = "";
			
			content.forEach(function (place) {
				item = document.createElement("li");
				
				note = document.createElement("span");
				name = document.createElement("span");
				
				item.className = place.visible ? "active" : "inactive";
				
				name.textContent = place.name;
				note.textContent = place.note;
				
				item.appendChild(name);
				item.appendChild(note);
				list.appendChild(item);
			});
		},
		render : function () {
			this.renderFlags(true);
			this.renderFlags(false);
			this.renderVisibilities();
		}
	};
	
	var units = {
		geoCoordinatesToDistance : function (topLeft, bottomRight) { // nur waagrecht oder senkrecht
			var topY = topLeft[0], bottomY = bottomRight[0];
			var leftX = topLeft[1], rightX = bottomRight[1];
			
			// TODO: Zwei Arten zur Berechnung des Maßstabs. Welche?
			console.log("Ausgerechnet anhand Breite: " + (111.32 * (topY - bottomY)));
			console.log("Ausgerechnet anhand Länge: " + (2 * Math.PI * 6371 * Math.cos((topY - (topY - bottomY) / 2) * Math.PI / 180)/360 * (rightX - leftX)));
			

			return 2 * Math.PI * 6371 * Math.cos((topY - (topY - bottomY) / 2) * Math.PI / 180)/360 * (rightX - leftX);
		},
		distanceToGeoCoordinates : function (distance, center) { // Sollte tun
			var oldDistance,
				coordinateDiffX, coordinateDiffY,
				distanceDiff,
				newTopLeft = [], newBottomRight = [];
			
			oldDistance = units.geoCoordinatesToDistance(map.coordinates.topLeft, center);
			distanceDiff = distance/oldDistance;
			
			coordinateDiffY = Math.abs(center[0] - map.coordinates.topLeft[0]);
			coordinateDiffX = Math.abs(center[1] - map.coordinates.topLeft[1]);
			
			newTopLeft[0] = center[0] + coordinateDiffY * distanceDiff;
			newBottomRight[0] = center[0] - coordinateDiffY * distanceDiff;
			newTopLeft[1] = center[1] - coordinateDiffX * distanceDiff;
			newBottomRight[1] = center[1] + coordinateDiffX * distanceDiff;
			
			return [newTopLeft, newBottomRight];
		},
		geoCoordinateToPixelCoordinate : function (latitude, longitude) {
			x = (latitude - map.coordinates.topLeft[0]) / (map.coordinates.bottomRight[0] - map.coordinates.topLeft[0]) * map.dimensions.width;
			y = (longitude - map.coordinates.topLeft[1]) / (map.coordinates.bottomRight[1] - map.coordinates.topLeft[1]) * map.dimensions.height;
			
			return [x,y];
		},
		pixelCoordinateToGeoCoordinate : function (x, y) { //TODO: Sascha
			var newX,newY
			newX=(x+map.panning.x)*map.scaling.scaleFactor;
			newY=(y+map.panning.y)*map.scaling.scaleFactor;
		
			latitude = ((newX*(map.coordinates.bottomRight[0] - map.coordinates.topLeft[0]))/map.dimensions.width)+map.coordinates.topLeft[0];
			longitude = ((newY*(map.coordinates.bottomRight[1] - map.coordinates.topLeft[1]))/map.dimensions.height)+map.coordinates.topLeft[1];
			
			return [latitude, longitude];
		}
	};
	
	return {
		init: function () {
			controller.init();
		},
		pan : function (x, y) {
			renderer.pan(x, y);
		},
		zoom : function (oldDistance, newDistance) {
			renderer.zoom(oldDistance, newDistance);
		},
		units : units
	,
		filter : function (filters) {
			renderer.filter(filters);
		}
	}
	
})();

Object.prototype.observe = function (property,handler) {
	var tempValue,
		oldValue;
		
	tempValue = this[property] || undefined;
	
	if ("defineProperty" in Object) {
		Object.defineProperty(this,property,{
			set : function (value) {
				oldValue = tempValue;
				tempValue = value;
				if (typeof handler === "function") {
					handler({ data : value, oldData : oldValue });
				}
			},
			get : function () {
				return tempValue;
			}
		});
	} else if ("__defineSetter" in this && "__defineGetter__" in this) {
		this.__defineSetter__(property,function (value) {
			oldValue = tempValue;
			tempValue = value;
			handler({ data : value, oldData : oldValue });
		});
		this.__defineGetter__(property,function () {
			return tempValue;
		});
	}
};

Array.prototype.unique = function () {
	var length = this.length,
		returnArray = [];
	
	this.forEach(function (value) {
		if (!returnArray.in_array(value)) {
			returnArray.push(value);
		}
	});
	
	return returnArray;
}

Object.prototype.equal = function (object) {
	var keys = Object.keys(this), tempResult,
		i = 0;
	
	for (i; i < keys.length; i++) {
		if (object[keys[i]] === "undefined") {
			return false;
		}
		
		if (this[keys[i]] instanceof Object) {
			tempResult = this[keys[i]].equal(object[keys[i]]);
			if (!tempResult) {
				return false;
			}
		} else if (!(this[keys[i]] instanceof Function)) {
			if (object[keys[i]] !== this[keys[i]]) {
				return false;
			}
		}
	}
	
	return true;
}

Array.prototype.in_array = function (value,remove) {
	var found = false,
		i = 0;
			
	for (i; i < this.length; i++) {
		if (this[i] instanceof Array) {
			found = this[i].in_array(value);
		} else if (this[i] instanceof Object && value instanceof Object) {
			found = this[i].equal(value);
		} else if (this[i] === value) {
			found = true;
		}
		
		if (found) {
			if (remove === true) {
				this.splice(i,1);
			}
			return true;
		}
	}
	
	return false;
}

window.addEventListener("DOMContentLoaded", function() {
	Karte.init();
}, false);