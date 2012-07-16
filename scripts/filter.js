var Sort = {
	filter: function (data, filter) {
		"use strict";
		
		var attribute, value, type,
			filteredData = data, tmpFilteredData = [],
			lastAttribute,
			i = 0;
			
		// first find duplicate filters, apply them and combine the results
		
		filter = filter.sort(function (a, b) { // first sort them for more efficiency
			return a.attribute > b.attribute ? -1 : 1;
		});
		
		filter.forEach(function (element, index, array) {
			if (element.attribute === lastAttribute) {
				i = index;
				
				Sort.applyFilter(filteredData, array[index - 1].attribute, array[index - 1].value, array[index - 1].type).forEach(function(element) {
					tmpFilteredData.push(element);
				});
				Sort.applyFilter(filteredData, element.attribute, element.value, element.type).forEach(function(element) {
					tmpFilteredData.push(element);
				});
				filter.splice(index - 1, 2);
				
				while (array.length > index && array[index].attribute === lastAttribute) {
					Sort.applyFilter(filteredData, array[i].attribute, array[i].value, array[i].type).forEach(function(element) {
						tmpFilteredData.push(element);
					});
					filter.splice(index, 1);
				}
				
				filteredData = tmpFilteredData.unique();
			}
			
			lastAttribute = element.attribute;
		});
		
		
		// now filter the rest of the data
			
		while (filter.length > 0) {
			attribute = filter[0].attribute;
			value = filter[0].value;
			type = filter[0].type;
			
			filteredData = Sort.applyFilter(filteredData, attribute, value, type);
			filter.splice(0, 1);
		}
		
		return filteredData;
	},
	sort: function (data, sortOrder, reverse) {
		"use strict";
		
		var attribute, applyReverse = Array.isArray(reverse), sortReverse,
			i = 0;
		
		data = data.sort(function (a, b) {
			i = 0;
			attribute = sortOrder[i];
			sortReverse = applyReverse ? reverse[i] : false;
			
			while (a[attribute] === b[attribute]) {
				attribute = sortOrder[++i];
				sortReverse = applyReverse ? reverse[++i] : false;
				if (!attribute) {
					return 0;
				}
			}
			
			return (a[attribute] > b[attribute] ? 1 : -1) * [-1,1][+!!sortReverse];
		});
		
		return data;
	},
	applyFilter: function (data, attribute, value, type) {
		"use strict";
		return data.filter(function(element, index) {
			switch (type) {
				case "equal":
					return element[attribute] === value;
				case "isnot":
					return element[attribute] !== value;
				case "greater":
					return element[attribute] > value;
				case "smaller":
					return element[attribute] < value;
				case "contains" :
					return element[attribute].search(new RegExp(value, "i")) !== -1;
			}
		});
	}
}