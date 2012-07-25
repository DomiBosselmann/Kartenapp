window.Karte = (function () {
	var loggedin = undefined;

	var constants = {
		locations : {
			maps : "http://karte.localhost/backend/php/svg_market.php",
			login : "http://karte.localhost/backend/php/login.php",
			save : "http://karte.localhost/backend/php/db.php?a=s",
			import : "http://karte.localhost/backend/php/db.php?a=i",
			export : "http://karte.localhost/backend/php/db.php?a=e"
		},
		math : {
			epsilon : Number.MIN_VALUE // Viele Grüße Herr Gröll!
		}
	};
	
	var map = {
		isLoaded : false,
		inInvalid : false,
		isInitial : true,
		dimensions : {
			width : undefined,
			height : undefined,
			maxWidth : undefined,
			maxHeight : undefined,
			currentWidth : undefined,
			currentHeight : undefined,
			x : undefined,
			y : undefined
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
			scaleFactor : 1,
			scalable : true
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
						name : "Großstadt",
						visible : true,
						paramName : "c",
						layerName : "cities",
						zIndex : 15,
						tooltip : "> 100.000 Einwohner"
					},
					towns : {
						name : "Kleinstadt",
						visible : false,
						paramName : "c1",
						layerName : "towns",
						zIndex : 14,
						tooltip : "> 10.000 Einwohner"
					},
					villages : {
						name : "Dorf",
						visible : false,
						paramName : "c2",
						layerName : "villages",
						zIndex : 13,
						tooltip : "≤ 10.000 Einwohner"
					},
					hamlets : {
						name : "Kleines Dorf",
						visible : false,
						paramName : "c3",
						layerName : "hamlets",
						zIndex : 12,
						tooltip : "< 1.000 Einwohner"
					},
					suburbs : {
						name : "Stadtteile",
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
						name : "Bundesland",
						visible : true,
						paramName : "b",
						layerName : "federal",
						zIndex : 1
					},
					counties : {
						name : "Landkreis",
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
						name : "Autobahn",
						visible : true,
						paramName : "s",
						layerName : "motorways",
						zIndex : 10
					},
					federal : {
						name : "Bundesstraße",
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
						name : "Fluss",
						visible : true,
						paramName : "w",
						layerName : "rivers",
						zIndex : 6
					},
					canals : {
						name : "Kanal",
						visible : false,
						paramName : "w1",
						layerName : "canals",
						zIndex : 5
					},
					namedLakes : {
						name : "See",
						visible : false,
						paramName : "w2",
						layerName : "namedLakes",
						zIndex : 4
					},
					allLakes : {
						name : "Wasserstelle",
						visible : false,
						paramName : "w3",
						layerName : "unnamedLakes",
						zIndex : 3
					}
				}
			}
		},
		places : [],
		routes : []
	};
	
	var controller = {
		uiElements : {
			aside : null,
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
			flaglist : null,
			places : null,
			routes : null,
			loginForm : null
		},
		init : function () {
			// UI-Elemente mit Referenzen versehen
			this.uiElements.aside = document.getElementsByTagName("aside")[0];
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
			this.uiElements.flaglist = document.getElementById("flaglist");
			this.uiElements.places = document.getElementById("places");
			this.uiElements.routes = document.getElementById("routes");
			this.uiElements.loginForm = document.getElementById("login");
			
			// EventListener hinzufügen
			this.uiElements.addButton.addEventListener("click", this.handler.flags.enableNewFlagMask, false);
			this.uiElements.searchButton.addEventListener("click", this.handler.handleSearch, false);
			this.uiElements.exportButton.addEventListener("click", this.handler.export.perform, false);
			this.uiElements.importButton.addEventListener("click", this.handler.import.enable, false);
			this.uiElements.saveButton.addEventListener("click", this.save, false);
			
			this.uiElements.searchField.addEventListener("keyup", controller.handler.handleSearchInput, false);
			this.uiElements.searchField.addEventListener("keydown", controller.handler.handleSearchInput, false);
			
			this.uiElements.mapScaler.addEventListener("mousedown", this.handler.enableScaling, false);
			
			this.uiElements.flaglist.addEventListener("drop", this.handler.import.viaDrop , false);
			
			this.uiElements.loginForm.addEventListener("submit", this.handler.performLogin, false);
			
			
			
			// Login-Status sicher wegschreiben und Manipulation verhindern
			loggedin = document.body.getAttribute("data-loggedin");
			
			// Drecks-Browser-Kompatibilität. Das ist in DOM2 spezifiziert, verdammt noch mal. Ironischerweise fixbar durch DOM4.
			var observer = ("WebKitMutationObserver" in window) ? new WebKitMutationObserver(controller.handler.observation) : (("MutationObserver" in window) ? new MutationObserver(controller.handler.observation) : undefined);
			
			if (observer !== undefined) {
				observer.observe(document.body, { attributes : true, subtree : false });
				observer.observe(this.uiElements.aside, { attributes : true, subtree : false });
			} else {
				document.body.addEventListener("DOMAttrModified", function (event) {
					controller.handler.detectManipulation({
						name : event.attrName,
						newValue : event.newValue,
						target : event.target
					});
				}, false);
				this.uiElements.aside.addEventListener("DOMAttrModified", function (event) {
					controller.handler.detectManipulation({
						name : event.attrName,
						newValue : event.newValue,
						target : event.target
					});
				}, false);
			}
			/*document.styleSheets[0].cssRules[2].observe("style", function (event) {
				controller.handler.detectManipulation({
					name : "stylesheet",
					target : undefined,
					newValue : document.styleSheets[0].cssRules[2]
				});
			});*/
			
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
				controller.uiElements.mapScaleText.textContent = event.data === Number.POSITIVE_INFINITY ? "Ungültiger Maßstab" : event.data.toFixed(2) + map.scaling.unit;
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
					sideView.renderFlags(undefined, true);
					sideView.renderFlags(undefined, true);
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
					event.stopPropagation();
				}
			},
			performSearch : function (event) {
				var filteredData = [];
				
				if (controller.uiElements.searchField.value === "") {
					// Wenn Suchfeld leer ist, kann die ganze View gerendert werden
					sideView.renderFlags(undefined, true);
					sideView.renderFlags(undefined, false);
				} else {
					// Suchausdruck durchsucht Namen und Notitz
					// Zunächst werden die Orte durchsucht
					filteredData = Sort.filter(map.places, [{ attribute : "name", type : "contains", value : controller.uiElements.searchField.value }]);
					sideView.renderFlags(filteredData, false);
					
					// Jetzt werden die Strecken durchsucht
					filteredData = Sort.filter(map.routes, [{ attribute : "name", type : "contains", value : controller.uiElements.searchField.value }]);
					sideView.renderFlags(filteredData, true);
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
				
				document.body.classList.add("dragging");
				
				document.addEventListener("mousemove", controller.handler.handleScaling, false);
				document.addEventListener("mouseup", controller.handler.finishScaling, false);
			},
			handleScaling : function (event) {
				// Aktuellen Maßstab berechnen
				var diff = event.pageX - map.scaling.scalerX;
				if (diff >= 100) {
					map.scaling.isScalable = false;
					diff = 100 - constants.math.epsilon;
				} else {
					map.scaling.isScalable = true;	
				}
				
				var scaleValue = (map.scaling.value/Math.abs(100 - diff)) * 100;
			
				// Karte skalieren
				
				renderer.zoom(map.scaling.zoomLevelValue, scaleValue);
								
				// Maßstab-UI anpassen
				var transform = "translate(%d)",
					transformValue;
				
				// Zoom-Level wegschreiben
				map.scaling.scaleFactor = map.scaling.zoomLevelValue / scaleValue;
				
				controller.uiElements.mapScaleText.textContent = scaleValue === Number.POSITIVE_INFINITY ? "Ungültiger Maßstab" : scaleValue.toFixed(2) + map.scaling.unit;
				
				var length = controller.uiElements.scalables.length;
				
				for (var i = 0; i < length; i++) {
					transformValue = (event.pageX - map.scaling.scalerX) * (i + 1)/4;
					controller.uiElements.scalables[i].setAttribute("transform",transform.replace(/%d/g, transformValue));
				}
			},
			finishScaling : function (event) {
				if (map.scaling.isScalable) {
					// Die Karte ist noch darstellbar und deshalb kann der Zoom-Vorgang abgeschlossen werden
					
					var diff = event.pageX - map.scaling.scalerX
					map.scaling.value = (map.scaling.value/Math.abs(100 - diff)) * 100;
					
					// Bei Bedarf neue Daten laden — TODO: Bedarf ermitteln
									
				} else {
					// Die Karte ist nicht mehr darstellbar, der Zoom-Vorgang wird unterbrochen und zurückgesetzt
					controller.uiElements.mapScaleText.textContent = map.scaling.value.toFixed(2) + map.scaling.unit;
					renderer.zoom(map.scaling.zoomLevelValue, map.scaling.value);
				}
				
				// Maßstab-UI anpassen
					
				controller.uiElements.mapScale.setAttribute("class","finishScaling");
								
				window.setTimeout(function () {
					var length = controller.uiElements.scalables.length;
				
					for (var i = 0; i < length; i++) {
						controller.uiElements.scalables[i].removeAttribute("transform");
					}
				}, 20);
				
				document.body.classList.remove("dragging");
				
				document.removeEventListener("mousemove", controller.handler.handleScaling, false);
				document.removeEventListener("mouseup", controller.handler.finishScaling, false);
			},
			scaleViaMouse : function (event) {
				var delta = event.wheelDelta / 45 || -event.detail / 90;
				var newScaleValue = (map.scaling.value/Math.abs(100 + delta)) * 100;
				
				map.scaling.value = newScaleValue;
				
				map.scaling.scaleFactor = map.scaling.zoomLevelValue / newScaleValue;
				renderer.zoom(map.scaling.zoomLevelValue, map.scaling.value);
				
			},
			enablePanning : function (event) {
				map.panning.startX = event.pageX - map.panning.x;
				map.panning.startY = event.pageY - map.panning.y;
				
				document.body.classList.add("dragging");
			
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
				
				document.body.classList.remove("dragging");
				
				// EventListener wieder entfernen
				document.removeEventListener("mousemove", controller.handler.handlePanning, false);
				document.removeEventListener("mouseup", controller.handler.finishPanning, false);
			},
			setVisibility : function (event, object) {
				if (object.zIndex === 1) {
					return;
				}
				
				object.visible = object.visible ? false : true;
				event.currentTarget.className = object.visible ? "active" : "inactive";
				
				renderer.filter(object, !object.visible);
			},
			loadMap : function (event) {
				var data = event.data,
					coordinates = data.getElementsByTagName("coords")[0],
					dimensions = data.getElementsByTagName("dimensions")[0];
				
				// Metadaten auslesen
				map.coordinates.topLeft = [parseFloat(coordinates.getAttribute("lat1")), parseFloat(coordinates.getAttribute("lon1"))];
				map.coordinates.bottomRight = [parseFloat(coordinates.getAttribute("lat2")), parseFloat(coordinates.getAttribute("lon2"))];
				map.dimensions.x = parseFloat(dimensions.getAttribute("x"));
				map.dimensions.y = parseFloat(dimensions.getAttribute("y"));
				map.dimensions.currentWidth = parseFloat(dimensions.getAttribute("width"));
				map.dimensions.currentHeight = parseFloat(dimensions.getAttribute("height"));
				//map.coordinates.center = [(map.coordinates.topLeft[0] - map.coordinates.bottomRight[0])/2 + map.coordinates.topLeft[0], (map.coordinates.topLeft[1] - map.coordinates.bottomRight[1])/2 + map.coordinates.topLeft[1]];
				
				if (map.isInvalid) {
					// Detaillevel hat sich geändert, altes Kartenrendering muss entfernt werden und neu gerendert werden.
				}

				
				// Maßstab neu berechnen
				var scaleFactor = map.scaling.scaleFactor;
				map.scaling.zoomLevelValue = units.geoCoordinatesToDistance(map.coordinates.topLeft, map.coordinates.bottomRight) / map.dimensions.currentWidth * 100;
				map.scaling.value = map.scaling.zoomLevelValue / map.scaling.scaleFactor;
				
				console.log(scaleFactor, map.scaling.value);
									
				// Renderer anstoßen
				renderer.start(data);
				
				if (map.isInitial) {
					document.getElementById("federal").addEventListener("mousedown", controller.handler.enablePanning, false);
			
					controller.uiElements.map.addEventListener("MozMousePixelScroll", controller.handler.scaleViaMouse, false);
					controller.uiElements.map.addEventListener("mousewheel", controller.handler.scaleViaMouse, false);
				}
				
				map.isInitial = false;
			},
			flags : {
				listReference : undefined,
				flagReference : undefined,
				pinReferences : [],
				x : [],
				y : [],
				altKey : undefined,
				flagObject : undefined,
				panningDiffX : undefined,
				panningDiffY : undefined,
				isAddingAllowed : true,
				enableNewFlagMask : function (event) {
					var container = document.createElement("ul");
					var newPlace = document.createElement("li");
					var newRoute = document.createElement("li");
					
					container.id = "addSelectionMask";
					
					newPlace.textContent = "Neuer Ort";
					newPlace.addEventListener("click", controller.handler.flags.enableAddNewPlace, false);
					
					newRoute.textContent = "Neue Strecke";
					newRoute.addEventListener("click", controller.handler.flags.enableAddNewRoute, false);
					
					container.appendChild(newPlace);
					container.appendChild(newRoute);
					
					document.body.appendChild(container); // Einbindung ändern
					
					event.stopPropagation();
					
					document.addEventListener("click", controller.handler.flags.checkNewFlagMask, false);
					document.addEventListener("keyup", controller.handler.flags.checkNewFlagMask, false);
				},
				checkNewFlagMask : function (event) {
					if (event.currentTarget !== document.getElementById("addSelectionMask") || event.keyCode === 27) {
						controller.handler.flags.disableNewFlagMask();
					}
				},
				disableNewFlagMask : function () {
					document.body.removeChild(document.getElementById("addSelectionMask"));
					document.removeEventListener("click", controller.handler.flags.checkNewFlagMask, false);
					document.removeEventListener("keyup", controller.handler.flags.checkNewFlagMask, false);
				},
				enableAddNewFlag : function (event) {
					controller.uiElements.toolbar.className = "addFlagEnabled";
					document.addEventListener("keyup", controller.handler.flags.checkAddNewFlag, false);
				},
				performAddNewPin : function (event, isRoute) {
					if (event.currentTarget === event.target) { // Event war nicht im Zeichenbereich von Bawü
						return false;
					}
					
					if (("SVGElementInstance" in window && event.target instanceof SVGElementInstance && event.target.correspondingUseElement.parentNode === renderer.flags) || event.target.parentNode === renderer.flags) { // War der Klick auf einem Flag? (Variante 1 für Webkit, 2 für FF)
						return false;
					}
					
					var pinReference = renderer.addPin();
										
					// 1 Für Chrome + Safarie 2 für FF
					var pinCounter = controller.handler.flags.x.push(event.offsetX ? event.offsetX : event.layerX);
					controller.handler.flags.y.push(event.offsetY ? event.offsetY : event.layerY);
					
					if (isRoute) {
						controller.handler.flags.pinReferences.push(pinReference);
						renderer.drawRoute(pinReference, controller.handler.flags.x[pinCounter - 1], controller.handler.flags.y[pinCounter - 1], controller.handler.flags.flagReference);
					} else {
						controller.handler.flags.flagReference = pinReference;
						renderer.drawPin(pinReference, controller.handler.flags.x[pinCounter - 1], controller.handler.flags.y[pinCounter - 1]);
					}
					
					pinReference.setAttribute("class", "hover");
					
					pinReference.addEventListener("mousedown", controller.handler.flags.enablePanning, false); // ORT?
					
					return true;
				},
				performAddMeta : function (event, isRoute) {
					var keypressHandler = isRoute ? controller.handler.flags.finishAddNewRoute : controller.handler.flags.finishAddNewPlace;
					var abortHandler = isRoute ? controller.handler.flags.abortAddNewRoute : controller.handler.flags.abortAddNewPlace;
					var checkHandler = isRoute ? controller.handler.flags.checkAddNewRoute : controller.handler.flags.checkAddNewPlace;
					
					controller.handler.flags.listReference = document.createElement("li");
					controller.handler.flags.listReference.title = "Klicken, um " + (isRoute ? "diese Strecke" : "diesen Ort") + " zu verbergen";
					controller.handler.flags.listReference.contentEditable = true;
					controller.handler.flags.listReference.addEventListener("keypress", keypressHandler, false);
					controller.handler.flags.listReference.addEventListener("keyup", checkHandler, false);
					controller.handler.flags.listReference.addEventListener("blur", abortHandler, false);
					
					if (isRoute) {
						controller.uiElements.routes.appendChild(controller.handler.flags.listReference);
					} else {
						controller.uiElements.places.appendChild(controller.handler.flags.listReference);
					}

					controller.handler.flags.listReference.focus();
				},
				checkAddNewFlag : function (event) {
					if (event.keyCode === 27) {
						controller.handler.flags.disableAddNewFlag();
					}
				},
				disableAddNewFlag : function (event) {
					controller.uiElements.toolbar.className = "";
					document.removeEventListener("keyup", controller.handler.flags.checkAddNewFlag, false);
					controller.uiElements.mapRoot.removeEventListener("click", controller.handler.flags.performAddNewRoute, false)
					controller.uiElements.mapRoot.removeEventListener("click", controller.handler.flags.performAddNewPlace, false)
				},
				enableAddNewPlace : function (event) {
					controller.handler.flags.enableAddNewFlag();
					controller.uiElements.mapRoot.addEventListener("click", controller.handler.flags.performAddNewPlace, false);
				},
				disableAddNewPlace : function (event) {
					controller.handler.flags.disableAddNewFlag();
				},
				performAddNewPlace : function (event) {
					if (!controller.handler.flags.isAddingAllowed) {
						return;
					}
					
					controller.handler.flags.isAddingAllowed = true;
					controller.handler.flags.altKey = event.altKey;
					
					if (controller.handler.flags.performAddNewPin(event)) {
						controller.handler.flags.isAddingAllowed = false;
						controller.handler.flags.performAddMeta(event);
					}
				},
				finishAddNewFlag : function (event) {
					// Wenn der Name leer ist, abbrechen
					if (controller.handler.flags.listReference.textContent === "") {
						event.preventDefault();
						return false;
					}
					
					// Blur-Handler entfernen (UI-Aktion für Abbruch)
					controller.handler.flags.listReference.removeEventListener("blur", controller.handler.flags.abortAddNewPlace, false);
					
					controller.handler.flags.listReference.blur();
					controller.handler.flags.flagReference.removeAttribute("class");
					controller.handler.flags.listReference.contentEditable = false;
				},
				finishAddNewPlace : function (event) {
					if (event.keyCode === 13 || event.keyCode === 39) {
						// Es war ein Enter. Oder ein Rechtspfeil
						
						controller.handler.flags.finishAddNewFlag(event);
						
						// Neuen Ort wegschreiben
						controller.handler.flags.flagObject = {
							name : controller.handler.flags.listReference.textContent,
							visible : true,
							note : "",
							coordinates : units.pixelCoordinateToGeoCoordinate(controller.handler.flags.x[0], controller.handler.flags.y[0]),
							flagReference : controller.handler.flags.flagReference,
							listReference : controller.handler.flags.listReference
						};
						
						var flagID = map.places.push(controller.handler.flags.flagObject);
						
						// flag mit ID versehen
						controller.handler.flags.flagReference.setAttribute("data-interimFlagID", flagID);
						controller.handler.flags.flagReference.setAttribute("data-type", "place");
						event.target.setAttribute("data-interimFlagID", flagID);
						event.target.setAttribute("data-type", "place");
						
						controller.handler.flags.flagReference.addEventListener("mouseover", controller.handler.flags.highlightListView, false);
						controller.handler.flags.flagReference.addEventListener("mouseout", controller.handler.flags.deHighlightListView, false);
						controller.handler.flags.listReference.addEventListener("mouseover", controller.handler.flags.highlightFlag, false);
						controller.handler.flags.listReference.addEventListener("mouseout", controller.handler.flags.deHighlightFlag, false);
						controller.handler.flags.listReference.addEventListener("click", controller.handler.flags.setVisibility, false);
						
						controller.handler.flags.isAddingAllowed = true;
						
						if (!controller.handler.flags.altKey && !event.shiftKey) {
							
							controller.handler.flags.disableAddNewPlace();
						}
					}
				},
				abortAddNewPlace : function (event) {
					// Blur-Handler entfernen (UI-Aktion für Abbruch)
					controller.handler.flags.listReference.removeEventListener("blur", controller.handler.flags.abortAddNewPlace, false);
					
					controller.uiElements.places.removeChild(controller.handler.flags.listReference);
					renderer.removePin(controller.handler.flags.flagReference);
					
					controller.handler.flags.isAddingAllowed = true;
				},
				checkAddNewPlace : function (event) {
					if (event.keyCode === 27) {
						// Es war ein Esc
						controller.handler.flags.abortAddNewPlace(event);
					} else {
						controller.handler.flags.finishAddNewPlace(event);
					}
					
					event.stopPropagation();
				},
				enableAddNewRoute : function (event) {
					controller.handler.flags.enableAddNewFlag();
					
					// Neue Gruppierung für Route
					controller.handler.flags.flagReference = renderer.addRoute();
					
					controller.uiElements.mapRoot.addEventListener("click", controller.handler.flags.performAddNewRoute, false);
					document.addEventListener("keyup", controller.handler.flags.finishAddNewRoutePins, false);
				},
				disableAddNewRoute : function (event) {
					controller.handler.flags.disableAddNewFlag();
				},
				performAddNewRoute : function (event) {
					if (!controller.handler.flags.isAddingAllowed) {
						return;
					}
					
					controller.handler.flags.isAddingAllowed = true;
					controller.handler.flags.performAddNewPin(event, true);
					
				},
				finishAddNewRoutePins : function (event) {
					if (event.keyCode === 13) {
						controller.handler.flags.performAddMeta(event, true);
						document.removeEventListener("keyup", controller.handler.flags.finishAddNewRoutePins, false);
					}
				},
				finishAddNewRoute : function (event) {
					if (event.keyCode === 13 || event.keyCode === 39) {
						// Es war ein Enter. Oder ein Rechtspfeil
						
						controller.handler.flags.finishAddNewFlag(event);
												
						var pins = [];
						controller.handler.flags.pinReferences.forEach(function (pin, index) {
							pins.push({
								coordinates : [controller.handler.flags.x[index], controller.handler.flags.y[index]],
								reference : pin
							});
						});
						
						// Neuen Route wegschreiben
						controller.handler.flags.flagObject = {
							name : controller.handler.flags.listReference.textContent,
							visible : true,
							note : "",
							distance : controller.handler.calculateDistance(pins).toFixed(1) + "km",
							flagReference : controller.handler.flags.flagReference,
							pins : pins,
							listReference : controller.handler.flags.listReference
						};
												
						var flagID = map.routes.push(controller.handler.flags.flagObject);
						
						// Distanz anzeigen
						controller.handler.flags.listReference.setAttribute("data-route-distance", "— " + controller.handler.flags.flagObject.distance);
						
						// flag mit ID versehen
						controller.handler.flags.flagReference.setAttribute("data-interimFlagID", flagID);
						controller.handler.flags.flagReference.setAttribute("data-type", "route");
						event.target.setAttribute("data-interimFlagID", flagID);
						event.target.setAttribute("data-type", "route");
						
						controller.handler.flags.flagReference.addEventListener("mouseover", controller.handler.flags.highlightListView, false);
						controller.handler.flags.flagReference.addEventListener("mouseout", controller.handler.flags.deHighlightListView, false);
						controller.handler.flags.listReference.addEventListener("mouseover", controller.handler.flags.highlightFlag, false);
						controller.handler.flags.listReference.addEventListener("mouseout", controller.handler.flags.deHighlightFlag, false);
						controller.handler.flags.listReference.addEventListener("click", controller.handler.flags.setVisibility, false);
						
						controller.handler.flags.isAddingAllowed = true;
												
						if (!event.shiftKey) {
							controller.handler.flags.disableAddNewRoute();
						}
						
						event.stopPropagation();						
					}
				},
				abortAddNewRoute : function (event) {
				
				},
				highlightFlag : function (event) {
					var flagObject = event.currentTarget.getAttribute("data-type") === "route" ? map.routes : map.places;
					flagObject[event.currentTarget.getAttribute("data-interimFlagID") - 1].flagReference.setAttribute("class", "hover");
				},
				highlightListView : function (event) {
					var flagObject = event.currentTarget.getAttribute("data-type") === "route" ? map.routes : map.places;
					flagObject[event.currentTarget.getAttribute("data-interimFlagID") - 1].listReference.classList.add("hover");
				},
				deHighlightFlag : function (event) { // Super Funktionsname
					var flagObject = event.currentTarget.getAttribute("data-type") === "route" ? map.routes : map.places;
					flagObject[event.currentTarget.getAttribute("data-interimFlagID") - 1].flagReference.removeAttribute("class");
				},
				deHighlightListView : function (event) {
					var flagObject = event.currentTarget.getAttribute("data-type") === "route" ? map.routes : map.places;
					flagObject[event.currentTarget.getAttribute("data-interimFlagID") - 1].listReference.classList.remove("hover");
				},
				enablePanning : function (event) {
					document.body.classList.add("dragging");
					
					// Aktuellen Translate ermitteln
					var transform, x = event.offsetX ? event.offsetX : event.layerX, y = event.offsetY ? event.offsetY : event.layerY;
					
					controller.handler.flags.flagObject = map.places[event.currentTarget.getAttribute("data-interimFlagID") - 1];
					controller.handler.flags.listReference = controller.handler.flags.flagObject.listReference;
					controller.handler.flags.flagReference = controller.handler.flags.flagObject.flagReference;
					
					transform = controller.handler.flags.flagReference.transform.baseVal.getItem(0);
					if (transform.type == SVGTransform.SVG_TRANSFORM_TRANSLATE) {
						controller.handler.flags.panningDiffX = x - transform.matrix.e,
						controller.handler.flags.panningDiffY = y - transform.matrix.f;
      				}
      				      									
					document.addEventListener("mousemove", controller.handler.flags.performPanning, false);
					document.addEventListener("mouseup", controller.handler.flags.finishPanning, false);
					
					controller.handler.flags.flagReference.removeEventListener("mouseover", controller.handler.flags.highlightListView, false);
					controller.handler.flags.flagReference.removeEventListener("mouseout", controller.handler.flags.deHighlightListView, false);
					
					event.stopPropagation();
					event.preventDefault();
				},
				performPanning : function (event) {
					var x = (event.offsetX ? event.offsetX : event.layerX) - controller.handler.flags.panningDiffX;
					var y = (event.offsetY ? event.offsetY : event.layerY) - controller.handler.flags.panningDiffY;
					controller.handler.flags.flagReference.setAttribute("transform", "translate(" + x + " " + y + ") scale(0.1)");
					
					event.stopPropagation();
					event.preventDefault();		
				},
				finishPanning : function (event) {
					var x = (event.offsetX ? event.offsetX : event.layerX) - controller.handler.flags.panningDiffX;
					var y = (event.offsetY ? event.offsetY : event.layerY) - controller.handler.flags.panningDiffY;
					
					controller.handler.flags.flagObject.coordinates = units.pixelCoordinateToGeoCoordinate(x, y);
					
					document.removeEventListener("mousemove", controller.handler.flags.performPanning, false);
					document.removeEventListener("mouseup", controller.handler.flags.finishPanning, false);
					
					event.stopPropagation();
					event.preventDefault();
					
					controller.handler.flags.listReference.classList.remove("hover");
					document.body.classList.remove("dragging");
					
					controller.handler.flags.flagReference.addEventListener("mouseover", controller.handler.flags.highlightListView, false);
					controller.handler.flags.flagReference.addEventListener("mouseout", controller.handler.flags.deHighlightListView, false);
				},
				setVisibility : function (event) {
					var isRoute = event.currentTarget.getAttribute("data-type") === "route" ? true : false;
					var flag = isRoute ? map.routes : map.places;
					
					controller.handler.flags.flagObject = flag[event.currentTarget.getAttribute("data-interimFlagID") - 1];
					controller.handler.flags.listReference = controller.handler.flags.flagObject.listReference;
					controller.handler.flags.flagReference = controller.handler.flags.flagObject.flagReference;
					
					controller.handler.flags.flagObject.visible = !controller.handler.flags.flagObject.visible;
					controller.handler.flags.listReference.title = "Klicken, um " + (isRoute ? "diese Strecke" : "diesen Ort") + " zu " + (controller.handler.flags.flagObject.visible ? "verbergen" : "anzuzeigen");
					event.currentTarget.className = controller.handler.flags.flagObject.visible ? "active" : "inactive";
					
					controller.handler.flags.flagReference.style.display = controller.handler.flags.flagObject.visible ? "" : "none"; // Sollte eventuell in den Renderer
				}
			},
			calculateDistance : function (pins) {
				var distance = 0;
				
				pins.forEach(function (pin, index) {
					// Zunächst wird der Abstand in Pixeln gemessen
					if (pins[index + 1] !== undefined) {
						distance += Math.sqrt(Math.pow(Math.abs(pin.coordinates[0] - pins[index + 1].coordinates[0]), 2) + Math.pow(Math.abs(pin.coordinates[1] - pins[index + 1].coordinates[1]), 2));
					}
				});
				
				// Jetzt erfolgt die Umrechnung in Kilometer
				distance = distance / 100 * map.scaling.value;
				
				return distance;
			},
			keyboardNavigation : function (event) {
				
				if (!map.isLoaded) { // Wenn die Karte nicht geladen ist, kann auch keine UI-Interaktion erfolgen
					return;
				}
				
				// Alle Tastaturkurzbefehle, die nicht zur Navigation in der Karte dienen (alle mit ctrl)
				if (event.ctrlKey) {
					switch (event.keyCode) {
						case 70 : 
							if (event.altKey) {
								controller.uiElements.searchField.value = "";
								sideView.renderFlags(true);
								sideView.renderFlags(false);
							} else {
								controller.handler.enableSearch();
							}
							
							break; // F
						case 73 : controller.handler.import.enable(); break; // I
						case 79 : controller.handler.export.perform(); break; // O
						case 83 : controller.save(); break; // S
						case 80 : controller.handler.flags.enableAddNewPlace(); break; // N
						case 82 : controller.handler.flags.enableAddNewRoute(); break; // R
					}
					
					return;
				}
			
				// Bei Pfeiltasten: Navigation durch die Karte
				if (event.keyCode >= 37 && event.keyCode <= 40) {
					var panValue = event.shiftKey ? 250 : (event.altKey ? 10 : 50);
					
					switch (event.keyCode) {
						case 37 : map.panning.x += panValue; break; // Links
						case 38 : map.panning.y += panValue; break; // Oben
						case 39 : map.panning.x -= panValue; break; // Rechts
						case 40 : map.panning.y -= panValue; break; // Unten
					}
					
					renderer.pan(map.panning.x, map.panning.y);
					
					return;
				}
				
				if (event.keyCode === 73) { // Ein I => Zoom in die Karte
					map.scaling.value = (map.scaling.value/Math.abs(100 + 30)) * 100;
				
					map.scaling.scaleFactor = map.scaling.zoomLevelValue / map.scaling.value;
					renderer.zoom(map.scaling.zoomLevelValue, map.scaling.value);
					
					return;
				}
				
				if (event.keyCode === 79) { // Ein O => Zoom aus der Karte
					map.scaling.value = (map.scaling.value/Math.abs(100 - 30)) * 100;
				
					map.scaling.scaleFactor = map.scaling.zoomLevelValue / map.scaling.value;
					renderer.zoom(map.scaling.zoomLevelValue, map.scaling.value);
					
					return;
				}
				
				if (event.keyCode) { // Eine 0 => Zurücksetzung der Zoomstufe
				
				}
			},
			import : {
				form : undefined,
				enable : function (event) {
					controller.handler.import.form = document.createElement("form");
					controller.handler.import.form.id = "importFlags";
					
					var fileInput = document.createElement("input");
					fileInput.type = "file";
					fileInput.name = "importFile";
					fileInput.accept = "text/xml";
					fileInput.addEventListener("change", controller.handler.import.perform, false);
					
					controller.handler.import.form.appendChild(fileInput);
					controller.uiElements.toolbar.appendChild(controller.handler.import.form);
					
					fileInput.click();
				},
				perform : function (event) {
					var data;
					
					if ("getFormData" in controller.handler.import.form) {
						// Firefox macht das einfacher
						data = controller.handler.import.form.getFormData();
					} else {
						data = new FormData();
						data.append("importFile", controller.handler.import.form.firstChild);
					}
					
					controller.import(data);
				},
				viaDrop : function (event) {
					event.preventDefault();
					event.stopPropagation();
					
					var files = event.dataTransfer.files;
					var length = files.length;
					
					for (var i = 0; i < length; i++) {
						var file = new FileReader();
						file.addEventListener("load", controller.handler.import.addFlags, false);
						file.readAsText(files[i]);
					}
				},
				addFlags : function (event) {
					var flags = new DOMParser().parseFromString(event.target.result, "text/xml");
					var routes = flags.getElementsByTagName("route");
					var places = flags.getElementsByTagName("place");
					var noRoutes = routes.length;
					var noPlaces = places.length;
					var i = 0;
					
					for (i; i < noRoutes; i++) {
						var name = routes[i].getElementsByTagName("name")[0].textContent;
						var length = routes[i].getElementsByTagName("length")[0].textContent
						var nodes = routes[i].getElementsByTagName("coord");
						var pins = [];
						
						var flagReference = renderer.addRoute();
						
						for (var j = 0; j < nodes.length; j++) {
							var latitude = parseFloat(nodes[j].getAttribute("lat"));
							var longitude = parseFloat(nodes[j].getAttribute("lon"));
							var position = units.geoCoordinateToPixelCoordinate(latitude, longitude);
														
							var pinReference = renderer.addPin();
							renderer.drawRoute(pinReference, position[0], position[1], flagReference);
					
							pins.push({ reference : pinReference, coordinates : position });
						}
						
						flagReference.addEventListener("mousedown", controller.handler.flags.enablePanning, false);
						flagReference.addEventListener("mouseover", controller.handler.flags.highlightListView, false);
						flagReference.addEventListener("mouseout", controller.handler.flags.deHighlightListView, false);
						
						// Zugehöriges Listen-Element erstellen
						var listItem = document.createElement("li");
								
						listItem.className = "active";
						listItem.textContent = name;
						listItem.setAttribute("data-route-distance", "— " + length);
						listItem.addEventListener("mouseover", controller.handler.flags.highlightFlag, false);
						listItem.addEventListener("mouseout", controller.handler.flags.deHighlightFlag, false);
						listItem.addEventListener("click", controller.handler.flags.setVisibility, false);
						//listItem.title = "Klicken, um " + (routes ? "diese Strecke" : "diesen Ort") + " zu " + (place.visible ? "verbergen" : "anzuzeigen");
						
						var flagID = map.routes.push({
							name : name,
							distance : length,
							pins : pins,
							visible : true,
							listReference : listItem,
							flagReference : flagReference
						});
																		
						// flag und Liste mit ID versehen
						listItem.setAttribute("data-interimFlagID", flagID);
						listItem.setAttribute("data-type", "route");
						flagReference.setAttribute("data-interimFlagID", flagID);
						flagReference.setAttribute("data-type", "route");
						
						controller.uiElements.routes.appendChild(listItem);
					}
					for (i = 0; i < noPlaces; i++) {
						var name = places[i].getElementsByTagName("name")[0].textContent;
						var latitude = parseFloat(places[i].getElementsByTagName("coord")[0].getAttribute("lat"));
						var longitude = parseFloat(places[i].getElementsByTagName("coord")[0].getAttribute("lon"));
						var position = units.geoCoordinateToPixelCoordinate(latitude, longitude);
						
						
						var flagReference = renderer.addPin();
						renderer.drawPin(flagReference, position[0], position[1]);
						
						flagReference.addEventListener("mousedown", controller.handler.flags.enablePanning, false);
						flagReference.addEventListener("mouseover", controller.handler.flags.highlightListView, false);
						flagReference.addEventListener("mouseout", controller.handler.flags.deHighlightListView, false);
						
						// Zugehöriges Listen-Element erstellen
						var listItem = document.createElement("li");
								
						listItem.className = "active";
						listItem.textContent = name;
						listItem.addEventListener("mouseover", controller.handler.flags.highlightFlag, false);
						listItem.addEventListener("mouseout", controller.handler.flags.deHighlightFlag, false);
						listItem.addEventListener("click", controller.handler.flags.setVisibility, false);
						
						var flagID = map.places.push({
							name : name,
							coordinates : position,
							visible : true,
							listReference : listItem,
							flagReference : flagReference
						});
						
						// flag und Liste mit ID versehen
						listItem.setAttribute("data-interimFlagID", flagID);
						listItem.setAttribute("data-type", "place");
						flagReference.setAttribute("data-interimFlagID", flagID);
						flagReference.setAttribute("data-type", "place");
						
						controller.uiElements.places.appendChild(listItem);
					}
					//sideView.renderFlags(true);
				}
			},
			export : {
				downloader : null,
				perform : function (event) {
					window.location.href = constants.locations.export;
				}
			},
			observation : function (mutations) {
				mutations.forEach(function (event) {
					controller.handler.detectManipulation({
						name : event.attributeName,
						newValue : event.target.getAttribute(event.attributeName),
						target : event.target
					});
				});
			},
			detectManipulation : function (event) {
				console.log(event);
				
				var wasManipulated = false;
				
				if (event.target === document.body && event.name === "data-loggedin" && event.newValue !== loggedin.toString()) {
					// Manipulation durch Attributänderung
					wasManipulated = true;
				} else if (event.name === "stylesheet" && event.target === undefined) {
					// Manipulation durch Löschen
					wasManipulated = true;
				} else if (event.target === controller.uiElements.aside && event.name === "style") {
					// Manipulation durch Style-Änderung
					wasManipulated = true;
				}
				
				if (wasManipulated) {
					alert("Netter Versuch ;)");
					window.location.reload();
				}
			},
			performLogin : function (event) {
			
				request = new XMLHttpRequest();
				request.open("post", constants.locations.login, true);
				request.send(new FormData(controller.uiElements.loginForm));
				request.onreadystatechange = function () {
					if (request.readyState === 4) {
						if (request.responseText) {
							var response = JSON.parse(request.responseText);
							
							if (response.success === true) {
								controller.handler.login();
							} else {
								alert(response.error);
							}
						}
					}
				}
								
				event.preventDefault();
				return false;
			},
			login : function () {
				loggedin = true;
				document.body.setAttribute("data-loggedin", "true");
			}
		},
		loadMap : function (latitude, longitude, layers, handler) {
			
			var parameters, params = [], requestURL = constants.locations.maps, request,
				key;
				
			map.isLoaded = false;
						
			// Parameter für die Übergabe zusammenschustern
			
			latitude = [49.79, 47.579];
			longitude = [7.484, 10.51];
			
			parameters = {
				/*lat1 : latitude[0],
				lat2 : latitude[1],
				lon1 : longitude[0],
				lon2 : longitude[1],*/
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
		},
		save : function () {			
			// Daten aufbereiten
			var data = new FormData();
			var places = [];
			var routes = [];
			
			// DOM-Referenzen entfernen
			map.places.forEach(function (place) {
				places.push({
					name : place.name,
					visible : place.visible,
					note : place.note,
					coordinates : place.coordinates
				});
			});
			
			map.routes.forEach(function (route) {
				var coordinates = [];
				route.pins.forEach(function (pin) {
					coordinates.push(units.pixelCoordinateToGeoCoordinate(pin.coordinates[0], pin.coordinates[1]));
				});
				
				routes.push({
					name : route.name,
					visible : route.visible,
					coordinates : coordinates,
					distance : "0km",
					note : route.note
				});
			});
			
			data.append("places", JSON.stringify(places));
			data.append("routes", JSON.stringify(routes));
						
			// Request absetzen
			request = new XMLHttpRequest();
			request.open("post", constants.locations.save, true);
			request.send(data);
			request.onreadystatechange = function () {
				if (request.readyState === 4) {
					console.log(request.responseText);
				}
			}
		},
		import : function (data) {
			console.log(data);
			// Request absetzen
			request = new XMLHttpRequest();
			request.open("post", constants.locations.import, true);
			request.send(data);
			request.onreadystatechange = function () {
				if (request.readyState === 4) {
					console.log(request.responseText);
				}
			}
		},
		export : function () {
			
		}
	};
	
	var renderer = {
		root : undefined,
		data : undefined,
		map : undefined,
		layers : undefined,
		flags : undefined,
		defs : undefined,
		clipPath : undefined,
		hasClipPath : false,
		start : function (data) {
			// Verwaltungsmethode für den Renderer
			this.data = data;
			this.map = controller.uiElements.mapRoot;
			this.flags = controller.uiElements.mapRoot.getElementById("flags");
			this.clipPath = controller.uiElements.mapRoot.getElementById("borderClip");
			this.defs = controller.uiElements.mapRoot.getElementsByTagName("defs")[0];
			
			this.parse();
			this.render();
			this.optimize();
			
			this.layers = controller.uiElements.mapRoot.getElementsByTagName("g"); // Notwendigkeit?
		},
		parse : function () {
			this.root = this.data.getElementsByTagName("svg")[0];
			this.data = this.root.childNodes;
			
			var defs = this.root.getElementsByTagName("defs")[0];
			if (defs !== undefined) {
				for (var i = 0; i < defs.childNodes.length; i++) {
					renderer.defs.appendChild(defs.childNodes[i]);
				}
				
				this.root.removeChild(defs);
			}
			
			
			if (this.root.getElementById("federal") !== null) {
				var path = this.root.getElementById("federal").childNodes[0].cloneNode();
				this.clipPath.appendChild(path);
				
				renderer.hasClipPath = true;
			}
		},
		render : function () {
			var data = Array.prototype.slice.call(this.data);
			
			data.forEach(function (node) {
				if (!map.isInitial && (node.nodeName === "dimensions" || node.nodeName === "coords")) {
					return;
				}
				
				if (renderer.hasClipPath && node.nodeName === "g" && node.id !== "federal") {
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
		addPin : function () {
			return document.createElementNS("http://www.w3.org/2000/svg", "use");
		},
		drawPin : function (pin, x, y, flagReference) {
			pin.setAttributeNS(null, "transform", "translate(" + x + " " + y + ") scale(0.1)"); 
			pin.setAttributeNS("http://www.w3.org/1999/xlink", "href", "#pin");
			
			if (flagReference) {
				flagReference.appendChild(pin);
			} else {
				renderer.flags.appendChild(pin);
			}
		},
		addRoute : function () {
			// Pfad und Gruppe generieren
			var flagReference = document.createElementNS("http://www.w3.org/2000/svg", "g");
			var path = document.createElementNS("http://www.w3.org/2000/svg", "path");
			
			flagReference.appendChild(path);
			renderer.flags.appendChild(flagReference);
			
			return flagReference;
		},
		drawRoute : function (newPin, x, y, flagReference) {
			// Pfad zum aktuellen Punkt verlängern
			var pathElement = flagReference.getElementsByTagName("path")[0];
			var path = pathElement.getAttribute("d");
			
			if (path === null) {
				// Pfad ist initial und wird zum ersten Mal gezeichnet
				path = "M " + x + " " + y;
			} else {
				path += " L " + x + " " + y;
			}
			
			pathElement.setAttribute("d", path)
			
			renderer.drawPin(newPin, x, y, flagReference);
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
							
							if (value.tooltip) {
								item.title = value.tooltip;
							}
							
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
		renderFlags : function (data, renderRoute) {			
			// Alle Pins unsichtbar setzen
			var flags = renderer.flags.childNodes;
			
			for (var i = 0; i < flags.length; i++) {
				if (flags[i].nodeType !== 3 && (renderRoute === true && flags[i].getAttribute("data-type") === "route" || renderRoute === false & flags[i].getAttribute("data-type") === "place")) {
					flags[i].style.display = "none";
				}
			}
			
			if (renderRoute === true) {
				controller.uiElements.routes.innerHTML = "";
				
				var data = data || map.routes;
				
				data.forEach(function (route, index) {
					item = document.createElement("li");
									
					item.className = route.visible ? "active" : "inactive";
					item.textContent = route.name;
					item.setAttribute("data-interimFlagID", index + 1);
					item.setAttribute("data-type", "route");
					item.setAttribute("data-route-distance", "— " + route.distance);
					item.addEventListener("mouseover", controller.handler.flags.highlightFlag, false);
					item.addEventListener("mouseout", controller.handler.flags.deHighlightFlag, false);
					item.addEventListener("click", controller.handler.flags.setVisibility, false);
					item.title = "Klicken, um diese Strecke zu " + (route.visible ? "verbergen" : "anzuzeigen");
					
					route.flagReference.style.display = route.visible ? "" : "none";
					
					controller.uiElements.routes.appendChild(item);
				});
			} else {
				controller.uiElements.places.innerHTML = "";
				
				var data = data || map.places;
				
				data.forEach(function (place, index) {
					item = document.createElement("li");
									
					item.className = place.visible ? "active" : "inactive";
					item.textContent = place.name;
					item.setAttribute("data-interimFlagID", index + 1);
					item.setAttribute("data-type", "place");
					item.addEventListener("mouseover", controller.handler.flags.highlightFlag, false);
					item.addEventListener("mouseout", controller.handler.flags.deHighlightFlag, false);
					item.addEventListener("click", controller.handler.flags.setVisibility, false);
					item.title = "Klicken, um diesen Ort zu " + (place.visible ? "verbergen" : "anzuzeigen");
					
					place.flagReference.style.display = place.visible ? "" : "none";
					
					controller.uiElements.places.appendChild(item);
				});
			}
		},
		render : function () {
			//this.renderFlags(true);
			//this.renderFlags(false);
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
			var y = (latitude - map.coordinates.topLeft[0]) / (map.coordinates.bottomRight[0] - map.coordinates.topLeft[0]) * map.dimensions.height;
			var x = (longitude - map.coordinates.topLeft[1]) / (map.coordinates.bottomRight[1] - map.coordinates.topLeft[1]) * map.dimensions.width;
			
			x = (x - map.panning.x + map.dimensions.x) * map.scaling.scaleFactor;
			y = (y - map.panning.y + map.dimensions.y) * map.scaling.scaleFactor;
			
			return [x, y];
		},
		pixelCoordinateToGeoCoordinate : function (x, y) {			
			var newX = (x - map.dimensions.x) / map.dimensions.currentWidth;
            var longitudeDiff = map.coordinates.bottomRight[1] - map.coordinates.topLeft[1];
            var longitude = map.coordinates.topLeft[1] + (longitudeDiff * newX);

            var newY = (y - map.dimensions.y) / map.dimensions.currentHeight;
            var latitudeDiff = map.coordinates.topLeft[0] - map.coordinates.bottomRight[0];
            var latitude = map.coordinates.topLeft[0] - (latitudeDiff * newY);

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
		units : units,
		getPlaces : function () {
			var value = [];
			
			map.places.forEach(function (place) {
				if (place.coordinates.length !== 0) {
					value.push(place.coordinates.join(","));
				}
			});
			
			return {
				topLeft : map.coordinates.topLeft,
				bottomRight : map.coordinates.bottomRight,
				width : map.dimensions.currentWidth,
				height : map.dimensions.currentHeight,
				places : value
			};
		},
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