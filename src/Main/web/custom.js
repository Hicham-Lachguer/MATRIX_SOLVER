function addRow() {
    var nbrRows = $("#t").find("tr").length;
    if (nbrRows < 10) {
        var row = $("#t tr:last").clone();
        row.find("input").val("00.00");
        $("#t").append(row);
    }
}
function removeRow() {
    var nbrRows = $("#t").find("tr").length;
    if (nbrRows > 1) {
        $("#t").find("tr:last").remove();
    }
}
function addCol() {
    var cols = $("#t tr:first td").length;
    if (cols < 10) {
        var rows = $("#t tr").length;
        for (var i = 0; i < rows; i++) {
            var col = $("#t tr td:first").clone();
            col.find("input").val("00.00");
            $("#t tr:nth-child(" + (i + 1) + ")").append(col);
        }
    }
}
function removeCol() {
    var cols = $("#t tr:first td").length;
    if (cols > 1) {
        var rows = $("#t tr").length;
        console.log(rows);
        for (var i = 0; i < rows; i++) {
            $("#t tr:nth-child(" + (i + 1) + ") td:last").remove();
        }
    }
}

function getData() {
    var data = "";
    var rows = $("#t tr").length;
    var cols = $("#t tr:first td").length;
    for (var i = 0; i < rows; i++) {
        var currentRow = $("#t tr:nth-child(" + (i + 1) + ")");
        for (var j = 0; j < cols; j++) {
            var currentCol = currentRow.find("td:nth-child(" + (j + 1) + ")");
            data += currentCol.find("input").val();
            if (j < cols - 1) data += ",";
        }
        if (i < rows - 1) data += ";";
    }
    return data;
}

function randomMatrix() {
    var rows = $("#t tr").length;
    var cols = $("#t tr:first td").length;
    for (var i = 0; i < rows; i++) {
        var currentRow = $("#t tr:nth-child(" + (i + 1) + ")");
        for (var j = 0; j < cols; j++) {
            var data = Math.random() * 160;
            var currentCol = currentRow
                .find("td:nth-child(" + (j + 1) + ")")
                .find("input")
                .val(parseInt(data));
        }
    }
}

///------------------------

function diagonalize(s) {

	
	var M=str2arr(s);	
	changeA(M);
    var m = M.length;
    var n = M[0].length;
    for (var k = 0; k < Math.min(m, n); ++k) {
        // Find the k-th pivot
        i_max = findPivot(M, k);
        if (M[(i_max, k)] == 0) throw "matrix is singular";
        swap_rows(M, k, i_max);
        // Do for all rows below pivot
        for (var i = k + 1; i < m; ++i) {
            // Do for all remaining elements in current row:
            var c = M[i][k] / M[k][k];
            for (var j = k + 1; j < n; ++j) {
                M[i][j] = M[i][j] - M[k][j] * c;
            }
            // Fill lower triangular matrix with zeros
            M[i][k] = 0;
        }
    }
    
    return arr2str(M);
}

function findPivot(M, k) {
    var i_max = k;
    for (var i = k + 1; i < M.length; ++i) {
        if (Math.abs(M[i][k]) > Math.abs(M[i_max][k])) {
            i_max = i;
        }
    }
    return i_max;
}

function swap_rows(M, i_max, k) {
    if (i_max != k) {
        var temp = A[i_max];
        A[i_max] = A[k];
        A[k] = temp;
    }
}


var  A=[[1,1],[1,1]]

function arr2str(M){
	var sol="";
	for(var i=0;i<M.length;i++){
    	for(var j=0;j<M[0].length;j++){
    		sol+=M[i][j];
    		if(j==(M[0].length-1))
    			continue;
    			sol+=",";
    	}
    	if(j==M.length-1)
    		continue;
    	 sol+=";";
    }
    return sol;
}
function str2arr(s){
	var M=[];
    
	var rows = s.split(";");
	var r=rows.length;
	var c= rows[0].split(',').length;

	for(var i=0;i<r;i++){
		M.push(Array(c));
	}

	for(var i=0;i<r;i++){
		var cu= rows[i].split(',');
		for(var j=0;j<c;j++){
			M[i][j]=parseFloat(cu[j]);
		}
	}
	return M;
}
function changeA(M){
	m=M.length;
	n=M[0].length;
	A=[];
	for(var i=0;i<m;i++){
		A.push(Array(n));
	}


	for(var i=0;i<m;i++){
		for(var j=0;j<n;j++){
			A[i][j]=M[i][j];
		}
	}
}


