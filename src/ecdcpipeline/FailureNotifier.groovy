package ecdcpipeline


class FailureNotifier {
  static final EMAIL = 1
  static final SLACK = 2

  String jobName
  def notificationChannels

  FailureNotifier(String jobName) {
    this.jobName = jobName
    this.notificationChannels = 0
  }

  def activateNotificationChannel(channel) {
    notificationChannels = notificationChannels | channel
  }

  def send(script, String msg) {
    if (notificationChannels & EMAIL) {
      def toEmails = [[$class: 'DevelopersRecipientProvider']]
      script.emailext body: '${DEFAULT_CONTENT}\n' + msg + '\n\nCheck console output at $BUILD_URL to view the results.',
        recipientProviders: toEmails,
        subject: '${DEFAULT_SUBJECT}'
    }

    if (notificationChannels & SLACK) {
      script.slackSend color: 'danger', message: "${jobName}: " + msg
    }
  }
}
