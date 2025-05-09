JFace Data Binding/FAQ
======================


### Where can I ask a question?

You can ask questions on the [Eclipse JFace newsgroup](http://www.eclipse.org/newsportal/thread.php?group=eclipse.platform.jface). When you post please prefix the title with "\[DataBinding\]".

### How do I report a bug?

Create an issue on [GitHub](https://github.com/eclipse-platform/eclipse.platform.ui/issues).

### Where can I find examples of how to use data binding?

Have a look at our collection of [Snippets](JFaceDataBinding.md#snippets).


### Where can I get the plugins?

The JFace Data Binding plug-ins can be found in any of the following distributions on the Eclipse [download page](http://download.eclipse.org/eclipse/downloads/):

*   Eclipse SDK
*   RCP Runtime/SDK
*   Platform Runtime/SDK

  
Just select the desired build (e.g. stable, integration, nightly) and download one of the above distributions.

The plug-ins or JAR files that you need are these:

*   org.eclipse.core.databinding
*   org.eclipse.core.databinding.observable
*   org.eclipse.core.databinding.property
*   org.eclipse.equinox.common
*   org.eclipse.jface.databinding (if your want SWT and JFace databinding)
*   org.eclipse.core.databinding.beans (if you want databinding to Java beans).

  
The databinding framework is accessible using Maven with these dependencies:

    <!-- The core databinding framework -->
    <dependency>
      <groupId>org.eclipse.platform</groupId>
      <artifactId>org.eclipse.core.databinding</artifactId>
      <version>1.9.0</version>
    </dependency>
    
    <!-- If you want databinding to Java beans -->
    <dependency>
      <groupId>org.eclipse.platform</groupId>
      <artifactId>org.eclipse.core.databinding.beans</artifactId>
      <version>1.6.100</version>
    </dependency>
    
    <!-- If you want JFace and SWT databinding -->
    <dependency>
      <groupId>org.eclipse.platform</groupId>
      <artifactId>org.eclipse.jface.databinding</artifactId>
      <version>1.11.100</version>
    </dependency>
    

 

### What is a Realm, and do I need to care?

The data binding core plug-in does not depend on SWT but needs to talk about "being on the UI thread" in the abstract. The class Realm is similar to SWT's Display class - you can call Realm.asyncExec(Runnable) to execute code within a realm (read: on the UI thread), but it does not have any UI-specific methods or fields. If your UI is the only source of changes to your model, then everything will happen on the UI thread anyway and you don't have to worry about realms. **Workbench#createAndRunWorkbench** will initialize the default realm to be associated with the SWT UI thread if you are writing an RCP application, or a plug-in for the Eclipse Platform. If you are writing an application that only uses SWT, data binding, and optionally JFace, you will have to provide the realm explicitly, or set up a default realm in your main method. All of our [Snippets](JFaceDataBinding.md#snippets) do this - they are standalone applications that just use SWT and JFace. For more information, see the wiki page about [Realm](JFaceDataBinding.md#Realm).

### Does Data Binding depend on Eclipse and the OSGi runtime?

The core Data Binding plug-in only depends on org.eclipse.equinox.common, but no other parts of Eclipse. It will run without OSGi.

See [Where can I get the plugins?](#Where-can-I-get-the-plugins) for information about downloading the framework.

See also [JFace Data Binding/Runtime Dependencies](JFaceDataBinding.md#runtime-dependencies).

### Does Data Binding depend on SWT?

No. Data Binding is meant to be UI toolkit agnostic and more specifically UI agnostic. There is default support for SWT and JFace but this is in the org.eclipse.jface.databinding project which is separate from the core Data Binding APIs that live in org.eclipse.core.databinding. For background and when the separation occurred in see [bug 153630](https://bugs.eclipse.org/bugs/show_bug.cgi?id=153630).

### How do I run the tests?

The main JUnit Test Suite is contained in the org.eclipse.jface.tests.databinding project. This project contributes the "JFace-Data Binding Test Suite" to the IDE. When invoked this suite will run all tests.

Steps:

1.  Ensure the org.eclipse.jface.tests.databinding project is in your workspace.
2.  Open the Run Dialog (Run->Open Run Dialog...).
3.  Expand the "JUnit Plug-in Test" entry.
4.  Select the "JFace-Data Binding Test Suite" entry and select "Run".

### Can JFace Data Binding run against older versions of Eclipse?

Yes, we try to keep the data binding bundles compatible with the previous release of Eclipse - for example, data binding released with Eclipse 3.3 will work against Eclipse 3.2. Depending upon your needs you might need to take an extra step because of the way the released version of the bundles is compiled. For example, the compiled org.eclipse.jface.databinding bundle as released with Eclipse 3.3 will only run against Eclipse 3.3, whereas the distributions of org.eclipse.core.databinding and org.eclipse.core.databinding.beans will run just fine against Eclipse 3.2.x. If wanting to run org.eclipse.jface.databinding against Eclipse 3.2.x you'll need to build it from source against your target platform. For more information as to why this is necessary see [bug 177476](https://bugs.eclipse.org/bugs/show_bug.cgi?id=177476).
