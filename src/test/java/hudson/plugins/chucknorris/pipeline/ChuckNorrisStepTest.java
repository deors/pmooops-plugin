package hudson.plugins.chucknorris.pipeline;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.jenkinsci.plugins.workflow.test.steps.SemaphoreStep;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.model.Statement;
import org.jvnet.hudson.test.RestartableJenkinsRule;

import hudson.model.Result;
import hudson.plugins.chucknorris.RoundhouseAction;
import hudson.plugins.chucknorris.Style;

public class ChuckNorrisStepTest {
	@Rule
	public RestartableJenkinsRule story = new RestartableJenkinsRule();

    @Test
    public void badAssChuckNorris() throws Exception {
		story.addStep(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				WorkflowJob p = story.j.jenkins.createProject(WorkflowJob.class, "p");
				p.setDefinition(new CpsFlowDefinition(
						"PMOoops()\n" +
						"semaphore 'wait'\n"
				));
				WorkflowRun b1 = p.scheduleBuild2(0).waitForStart();
				SemaphoreStep.waitForStart("wait/1", b1);
				SemaphoreStep.failure("wait/1", new Exception());

				story.j.waitForCompletion(b1);
				story.j.assertBuildStatus(Result.FAILURE, b1);

				RoundhouseAction action = b1.getAction(RoundhouseAction.class);
				assertEquals(Style.BAD_ASS, action.getStyle());
				assertNotNull(action.getFact());
			}
		});
    }

    @Test
    public void alertChuckNorris() throws Exception {
		story.addStep(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				WorkflowJob p = story.j.jenkins.createProject(WorkflowJob.class, "p");
				p.setDefinition(new CpsFlowDefinition(
						"PMOoops()\n" +
						"semaphore 'wait'\n"
				));
				WorkflowRun b1 = p.scheduleBuild2(0).waitForStart();
				SemaphoreStep.waitForStart("wait/1", b1);
				b1.setResult(Result.UNSTABLE);
				SemaphoreStep.success("wait/1", null);

				story.j.waitForCompletion(b1);
				story.j.assertBuildStatus(Result.UNSTABLE, b1);

				RoundhouseAction action = b1.getAction(RoundhouseAction.class);
				assertEquals(Style.ALERT, action.getStyle());
				assertNotNull(action.getFact());
			}
		});
    }

    @Test
    public void thumbsUpChuckNorris() throws Exception {
		story.addStep(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				WorkflowJob p = story.j.jenkins.createProject(WorkflowJob.class, "p");
				p.setDefinition(new CpsFlowDefinition(
						"PMOoops()\n" +
						"semaphore 'wait'\n"
				));
				WorkflowRun b1 = p.scheduleBuild2(0).waitForStart();
				SemaphoreStep.waitForStart("wait/1", b1);
				SemaphoreStep.success("wait/1", null);

				story.j.waitForCompletion(b1);
				story.j.assertBuildStatus(Result.SUCCESS, b1);

				RoundhouseAction action = b1.getAction(RoundhouseAction.class);
				assertEquals(Style.THUMB_UP, action.getStyle());
				assertNotNull(action.getFact());
			}
		});
    }

    @Test
    public void projectPageChuckNorris() throws Exception {
		story.addStep(new Statement() {
			@Override
			public void evaluate() throws Throwable {
				WorkflowJob p = story.j.jenkins.createProject(WorkflowJob.class, "p");
				p.setDefinition(new CpsFlowDefinition(
						"PMOoops()\n" +
						"semaphore 'wait'\n"
				));
				WorkflowRun b1 = p.scheduleBuild2(0).waitForStart();
				SemaphoreStep.waitForStart("wait/1", b1);

				WorkflowRun b2 = p.scheduleBuild2(0).waitForStart();
				SemaphoreStep.waitForStart("wait/2", b2);

				SemaphoreStep.success("wait/1", null);
				SemaphoreStep.failure("wait/2", new Exception());

				story.j.waitForCompletion(b1);
				story.j.assertBuildStatus(Result.SUCCESS, b1);

				story.j.waitForCompletion(b2);
				story.j.assertBuildStatus(Result.FAILURE, b2);

				RoundhouseAction action1 = b1.getAction(RoundhouseAction.class);
				assertEquals(Style.THUMB_UP, action1.getStyle());
				assertNotNull(action1.getFact());

				RoundhouseAction action2 = b2.getAction(RoundhouseAction.class);
				assertEquals(Style.BAD_ASS, action2.getStyle());
				assertNotNull(action2.getFact());

				// Make sure we get the action from the last completed build (jenkins default behaviour is last successful build)
				RoundhouseAction projectAction = p.getAction(RoundhouseAction.class);
				assertEquals(Style.BAD_ASS, projectAction.getStyle());
				assertNotNull(projectAction.getFact());
			}
		});
    }
}
