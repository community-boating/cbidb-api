if (!Array.prototype.partition) {
	Array.prototype.partition = function(predicate) {
		var pass = [];
		var fail = [];
		for (var i = 0; i < this.length; i++) {
			var e = this[i];
			if (predicate(e)) pass.push(e);
			else fail.push(e);
		}
		return [pass, fail];
	}
}

var gulp = require('gulp');
var fs = require('fs');
var concat = require('gulp-concat');
var ini = require('ini');

const SRC_DIR = 'src';
const DIST_DIR = 'dist';

function build(routeFiles) {
	gulp.src(routeFiles)
		.pipe(concat("routes"))
		.pipe(gulp.dest(DIST_DIR))

}

gulp.task('build', function() {
    var secLevel = ini.parse(fs.readFileSync('../private/server-properties', 'utf-8')).routesSecurityLevel;
	var routeFiles = getRouteFiles(secLevel);
	build(routeFiles);
})

function getRouteFiles(secLevel) {
	console.log("Building routes with security level " + secLevel)
	var routeFiles = fs.readdirSync(SRC_DIR).partition(function(e) {
		var regex = /^([0-9]+)\_.*$/
		var result = regex.exec(e)
		return result && parseInt(result[1]) <= secLevel
	})
	console.log("Building:\n" + routeFiles[0].map(e => "*  " + e).join("\n"))
	console.log("Discarding:\n" + routeFiles[1].map(e => "X  " + e).join("\n"))
	return routeFiles[0].map(e => "./" + SRC_DIR + "/" + e);
}
