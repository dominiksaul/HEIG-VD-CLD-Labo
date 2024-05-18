# Task 3 - Add and exercise resilience

By now you should have understood the general principle of configuring, running and accessing applications in Kubernetes. However, the above application has no support for resilience. If a container (resp. Pod) dies, it stops working. Next, we add some resilience to the application.

## Subtask 3.1 - Add Deployments

In this task you will create Deployments that will spawn Replica Sets as health-management components.

Converting a Pod to be managed by a Deployment is quite simple.

  * Have a look at an example of a Deployment described here: <https://kubernetes.io/docs/concepts/workloads/controllers/deployment/>

  * Create Deployment versions of your application configurations (e.g. `redis-deploy.yaml` instead of `redis-pod.yaml`) and modify/extend them to contain the required Deployment parameters.

  * Again, be careful with the YAML indentation!

  * Make sure to have always 2 instances of the API and Frontend running. 

  * Use only 1 instance for the Redis-Server. Why?

    > Because we use Redis as a storage. That means it is a stateful instance. If we had two instances we would have to check to synchronize them.

  * Delete all application Pods (using `kubectl delete pod ...`) and replace them with deployment versions.

  * Verify that the application is still working and the Replica Sets are in place. (`kubectl get all`, `kubectl get pods`, `kubectl describe ...`)

## Subtask 3.2 - Verify the functionality of the Replica Sets

In this subtask you will intentionally kill (delete) Pods and verify that the application keeps working and the Replica Set is doing its task.

Hint: You can monitor the status of a resource by adding the `--watch` option to the `get` command. To watch a single resource:

```sh
$ kubectl get <resource-name> --watch
```

To watch all resources of a certain type, for example all Pods:

```sh
$ kubectl get pods --watch
```

You may also use `kubectl get all` repeatedly to see a list of all resources.  You should also verify if the application stays available by continuously reloading your browser window.

  * What happens if you delete a Frontend or API Pod? How long does it take for the system to react?
    > It immediately terminates the instance, a new pod is restarted directly after.
    
  * What happens when you delete the Redis Pod?

    > It terminates the redis instance and directly restarts a new one. However, how it's a new instance the data we've previously entered to the ToDo application are lost.
    
  * How can you change the number of instances temporarily to 3? Hint: look for scaling in the deployment documentation

    > By using the following command: kubectl scale deployment/<deploy-name> --replicas=<desired-number-of-replicas>
    kubectl scale deployment/api-deployment --replicas=3
    
  * What autoscaling features are available? Which metrics are used?

    > Available features:
      - Horizontal Pod Autoscaler (HPA): HPA automatically scales the number of pods in a deployment based on observed CPU utilization (or, more recently, custom metrics).
      - Vertical Pod Autoscaler (VPA): VPA adjusts the CPU and memory requests of pods to better match their actual usage.
      - Cluster Autoscaler: This feature automatically adjusts the size of the Kubernetes cluster itself by adding or removing nodes based on the resource demands of the pods and other factors such as node utilization, memory, or custom metrics.
      - Custom Metrics Autoscaling: Kubernetes supports autoscaling based on custom metrics using the Metrics API. This allows users to define their own metrics, such as queue length, request latency, or any other relevant metric, and use them for autoscaling decisions.
    > Metrics:
      - CPU Utilization: This is the most commonly used metric for autoscaling. It measures the CPU usage of pods and triggers scaling actions when the usage exceeds or falls below a certain threshold.
      - Memory Utilization: Similar to CPU, memory usage can also be used as a metric for autoscaling. When memory usage exceeds a threshold, additional pods may be added, and when it falls below, pods may be scaled down.
      - Custom Metrics: These are user-defined metrics that can be anything relevant to the application's performance or resource usage. Examples include queue length, response time, or any other business-specific metric.
      - Object Metrics: These metrics are specific to the resources managed by Kubernetes, such as the number of items in a database, messages in a queue, etc. They can be used to trigger autoscaling based on the state of these resources.

  * How can you update a component? (see "Updating a Deployment" in the deployment documentation)

    > Using the command : kubectl edit deployment/<deploy-name>

## Subtask 3.3 - Put autoscaling in place and load-test it

On the GKE cluster deploy autoscaling on the Frontend with a target CPU utilization of 30% and number of replicas between 1 and 4. 
> We use the following command: kubectl autoscale deployment/frontend-deployment --min=1 --max=4 --cpu-percent=30


Load-test using Vegeta (500 requests should be enough).

> [!NOTE]
>
> - The autoscale may take a while to trigger.
>
> - If your autoscaling fails to get the cpu utilization metrics, run the following command
>
>   - ```sh
>     $ kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml
>     ```
>
>   - Then add the *resources* part in the *container part* in your `frontend-deploy` :
>
>   - ```yaml
>     spec:
>       containers:
>         - ...:
>           env:
>             - ...:
>           resources:
>             requests:
>               cpu: 10m
>     ```
>

## Deliverables

Document your observations in the lab report. Document any difficulties you faced and how you overcame them. Copy the object descriptions into the lab report.

> // TODO

```````sh
// TODO object descriptions
```````

```yaml
# redis-deploy.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: redis-deployment
  labels:
    component: redis
    app: todo
spec:
  replicas: 1
  selector:
    matchLabels:
      component: redis
  template:
    metadata:
      labels:
        component: redis
        app: todo
    spec:
      containers:
        - name: redis
          image: redis
          ports:
            - containerPort: 6379
          args:
            - redis-server
            - --requirepass ccp2
            - --appendonly yes
```

```yaml
# api-deploy.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: api-deployment
  labels:
    component: api
    app: todo
spec:
  replicas: 2
  selector:
    matchLabels:
      component: api
  template:
    metadata:
      labels:
        component: api
        app: todo
    spec:
      containers:
        - name: api
          image: icclabcna/ccp2-k8s-todo-api
          ports:
            - containerPort: 8081
          env:
            - name: REDIS_ENDPOINT
              value: redis-svc
            - name: REDIS_PWD
              value: ccp2
```

```yaml
# frontend-deploy.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: frontend-deployment
  labels:
    component: frontend
    app: todo
spec:
  replicas: 2
  selector:
    matchLabels:
      component: frontend
  template:
    metadata:
      labels:
        component: frontend
        app: todo
    spec:
      containers:
        - name: frontend
          image: icclabcna/ccp2-k8s-todo-frontend
          ports:
            - containerPort: 8080
          env:
            - name: API_ENDPOINT_URL
              value: http://api-svc:8081
          resources:
            requests:
              cpu: 10m
```

```
Output of describe of autoscaler

gdomingo@CI39975 files % kubectl describe horizontalpodautoscalers.autoscaling
Name:                                                  frontend-deployment
Namespace:                                             default
Labels:                                                <none>
Annotations:                                           <none>
CreationTimestamp:                                     Sat, 18 May 2024 15:21:33 +0200
Reference:                                             Deployment/frontend-deployment
Metrics:                                               ( current / target )
  resource cpu on pods  (as a percentage of request):  0% (0) / 30%
Min replicas:                                          1
Max replicas:                                          4
Deployment pods:                                       4 current / 4 desired
Conditions:
  Type            Status  Reason               Message
  ----            ------  ------               -------
  AbleToScale     True    ScaleDownStabilized  recent recommendations were higher than current one, applying the highest recent recommendation
  ScalingActive   True    ValidMetricFound     the HPA was able to successfully calculate a replica count from cpu resource utilization (percentage of request)
  ScalingLimited  True    TooManyReplicas      the desired replica count is more than the maximum replica count
Events:
  Type     Reason                   Age                   From                       Message
  ----     ------                   ----                  ----                       -------
  Warning  FailedGetResourceMetric  20m (x10 over 22m)    horizontal-pod-autoscaler  missing request for cpu
  Warning  FailedGetResourceMetric  2m39s (x71 over 22m)  horizontal-pod-autoscaler  No recommendation

```