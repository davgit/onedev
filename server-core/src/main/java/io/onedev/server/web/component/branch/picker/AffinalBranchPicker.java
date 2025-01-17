package io.onedev.server.web.component.branch.picker;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;

import io.onedev.server.OneDev;
import io.onedev.server.entitymanager.ProjectManager;
import io.onedev.server.model.Project;
import io.onedev.server.persistence.dao.Dao;
import io.onedev.server.security.permission.ReadCode;
import io.onedev.server.util.ProjectCollection;
import io.onedev.server.web.component.project.ProjectPicker;

@SuppressWarnings("serial")
public abstract class AffinalBranchPicker extends Panel {

	private Long projectId;
	
	private String branch;
	
	public AffinalBranchPicker(String id, Long projectId, String branch) {
		super(id);
		
		this.projectId = projectId;
		this.branch = branch;
	}
	
	private void newBranchPicker(@Nullable AjaxRequestTarget target) {
		BranchPicker branchPicker = new BranchPicker("branchPicker", new LoadableDetachableModel<Project>() {

			@Override
			protected Project load() {
				return getProject();
			}
			
		}, branch) {

			@Override
			protected void onSelect(AjaxRequestTarget target, String branch) {
				AffinalBranchPicker.this.onSelect(target, getProject(), branch);
			}

		};
		if (target != null) {
			replace(branchPicker);
			target.add(branchPicker);
		} else {
			add(branchPicker);
		}
	}
	
	private Project getProject() {
		return OneDev.getInstance(Dao.class).load(Project.class, projectId);
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		add(new ProjectPicker("projectPicker", new LoadableDetachableModel<ProjectCollection>() {

			@Override
			protected ProjectCollection load() {
				Project project = OneDev.getInstance(Dao.class).load(Project.class, projectId);
				List<Project> affinals = project.getForkRoot().getForkChildren();
				affinals.add(0, project.getForkRoot());
				ProjectCollection projects = OneDev.getInstance(ProjectManager.class)
						.getPermittedProjects(new ReadCode());
				for (Iterator<Project> it = affinals.iterator(); it.hasNext();) {
					if (!projects.getIds().contains(it.next().getId()))
						it.remove();
				}
				return new ProjectCollection(projects.getCache(), 
						affinals.stream().map(it->it.getId()).collect(Collectors.toList()));
			}
			
		}) {

			@Override
			protected void onSelect(AjaxRequestTarget target, Project project) {
				projectId = project.getId();
				branch = project.getDefaultBranch();
				newBranchPicker(target);
				AffinalBranchPicker.this.onSelect(target, project, branch);
			}

			@Override
			protected Project getCurrent() {
				return OneDev.getInstance(ProjectManager.class).load(projectId);
			}
			
		});
		newBranchPicker(null);
		
		setOutputMarkupId(true);
	}
	
	protected abstract void onSelect(AjaxRequestTarget target, Project project, String branch);
	
}
