package workspaceexample.xwt;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IPathVariableManager;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.e4.xwt.DefaultLoadingContext;
import org.eclipse.e4.xwt.IConstants;
import org.eclipse.e4.xwt.IXWTLoader;
import org.eclipse.e4.xwt.XWT;
import org.eclipse.e4.xwt.annotation.UI;
import org.eclipse.e4.xwt.forms.XWTForms;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class NaviComposite extends Composite {

	IWorkspaceRoot workspace = ResourcesPlugin.getWorkspace().getRoot();
	
	@UI Text projectText, folderNameButton, countOfProjectText, linkResourceText;

	private IProject project;
	
	public NaviComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout());
		// load XWT
		String name = NaviComposite.class.getSimpleName()
				+ IConstants.XWT_EXTENSION_SUFFIX;
		try {
			URL url = NaviComposite.class.getResource(name);
			Map<String, Object> options = new HashMap<String, Object>();
			options.put(IXWTLoader.CLASS_PROPERTY, this);
			options.put(IXWTLoader.CONTAINER_PROPERTY, this);
			XWT.setLoadingContext(new DefaultLoadingContext(this.getClass()
					.getClassLoader()));
			XWTForms.loadWithOptions(url, options);
		} catch (Throwable e) {
			throw new Error("Unable to load " + name, e);
		}
	}

	public void onCountOfProjectsButtonSelection(Event event) {
		IProject[] projects = workspace.getProjects();
		countOfProjectText.setText(projects.length+"");
	}
	public void onCreateProjectButtonSelection(Event event){
		project = workspace.getProject(projectText.getText());
		try {
			if(project.exists())
				MessageDialog.openInformation(new Shell(), "Folder create", "Folder exist, please give another name!");
			else{
				project.create(null);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
	}
	public void onCreateFolderInProjectButtonSelection(Event event) {
		try {
			workspace.getProject(projectText.getText()).open(null);
			project.getFolder(folderNameButton.getText()).create(true, true, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	public void onOpenWorkspaceButtonSelection(Event event) {
		try {
			Desktop.getDesktop().open(new File(workspace.getLocation().toOSString()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void onOpenLinkResourceButtonSelection(Event event) {
		String linkResourceFilePath=new FileDialog(new Shell(), SWT.OPEN).open();
	
		if(linkResourceFilePath!=null)
			linkResourceText.setText(linkResourceFilePath);
	}
	public void onLinkResourceButtonSelection(Event event) {
		 IProject project = workspace.getProject(projectText.getText());//assume this exists
		   IFolder link = project.getFolder(folderNameButton.getText());
		   IFile child = link.getFile(linkResourceText.getText());
		      try {
		    	InputStream source = new FileInputStream(linkResourceText.getText());
				child.create(source, true, null);
			} catch (CoreException | FileNotFoundException e) {
				e.printStackTrace();
			}
	}
}
