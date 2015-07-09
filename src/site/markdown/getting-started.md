# Getting Started with Decisions

Omny Link Decisions is a light-weight tool to define and execute decision tables. It's written in Java but intended for use anywhere web technologies exist.

## Quick start - 5 minute demo

### Pre-requisites
  
  1. You will need a Java runtime installed, download from Oracle [here](http://todo) or using your package manager.  
  
### Steps 

  1. Download jar [here](TODO)
  2. Double-click the jar or run from the command line 
     ```
       java -jar decisions-x.y.z.jar
     ```
  3. Open your browser to (http://localhost:8081)[http://localhost:8081]
  4. Login with the development username and password (admin/admin)
  5. Example decisions are pre-installed, view the list at: (http://localhost:8081/decisions.html)
  6. Click the pencil icon to open a decision table and see the rules that make it up 
  7. Invoke a decision with test data by clicking the play icon
  
### What next? 

You'll probably want to think about:

  - Writing your own decisions  

    - (Here)[tutorials/first-decision-table.html] is a tutorial on how to build a simple decision table that introduces the key ideas and features

  - Integrating decisions into your applications or web sites

    - Easiest option is to use our WordPress plugin to build forms that can invoke your decisions when the user submits them explained (here)[wordpress.html]
    - If you're already executing your business processes with the Activiti BPMN engine you can see how to invoke decisions from the processes (here)[client/activiti.html] 
    - To invoke a decision directly from your web site using a JavaScript client continue (here)[clients/javascript.html]

  - Get involved by reporting bugs, writing documentation or submitting a patch. Learn how (here)[community.html] 