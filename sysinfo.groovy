// filebot -script fn:sysinfo

// FileBot 2.62 (r993)
println Settings.getApplicationIdentifier()

// JNA Native: 3.5.0
try {
	print 'JNA Native: '
	println com.sun.jna.Native.nativeVersion
} catch(Throwable error) {
	println error.cause
}

// MediaInfo: MediaInfoLib - v0.7.48
try {
	print 'MediaInfo: '
	println MediaInfo.version()
} catch(Throwable error) {
	println error.cause
}

// 7-Zip-JBinding: OK
try {
	print '7-Zip-JBinding: '
	net.sourceforge.filebot.archive.SevenZipLoader.requireNativeLibraries() // load 7-Zip-JBinding native libs
	println 'OK'
} catch(Throwable error) {
	println error
}

// chromaprint-tools
try {
	print 'chromaprint-tools: '
	def fpcalc = AcoustID.getChromaprintCommand()
	def version = [fpcalc, '-version'].execute().text.trim() ?: 'fpcalc -version failed'
	println "$version ($fpcalc)"
} catch(Throwable error) {
	println error
}

// Extended File Attributes
try {
	print 'Extended Attributes: '
	if (Settings.useExtendedFileAttributes()){
		// create new temp file
		def f = new File(Settings.getApplicationFolder(), '.xattr-test')
		f.createNewFile() && f.deleteOnExit()

		// xattr write, read and verify
		def xattr = new MetaAttributes(f)
		def payload = new Date()
		xattr.setObject(payload)
		assert xattr.getObject() == payload
		println 'OK'
	} else {
		println 'DISABLED'
	}
} catch(Throwable error) {
	println error
}

// GIO and GVFS
try {
	if (Settings.useGVFS()) {
		print 'GVFS: '
		assert net.sourceforge.filebot.gio.GVFS.defaultVFS != null
		println 'OK'
	}
} catch(Throwable error) {
	println error
}

// Groovy Engine: 2.1.7
println 'Groovy Engine: ' + groovy.lang.GroovySystem.version

// Java(TM) SE Runtime Environment 1.6.0_30 (headless)
println 'JRE: ' + Settings.getJavaRuntimeIdentifier()

// 32-bit Java HotSpot(TM) Client VM
println String.format('JVM: %d-bit %s', com.sun.jna.Platform.is64Bit() ? 64 : 32, _system['java.vm.name'])

// Windows 7 (x86)
println String.format('OS: %s (%s)', _system['os.name'], _system['os.arch'])

// print uname -a if available
try {
	println String.format('uname: %s', ['uname', '-a'].execute().text.trim())
} catch(Throwable error) {
	// ignore
}


// check for updates
try {
	def update = new XmlSlurper().parse('http://app.filebot.net/update.xml')
	def latestRev = update.revision.text() as int
	def latestApp  = update.name.text()

	if (latestRev > Settings.getApplicationRevisionNumber()) {
		println "\n--- UPDATE AVAILABLE: $latestApp (r$latestRev) ---\n"
	}
} catch(Throwable error) {
	// ignore
}
